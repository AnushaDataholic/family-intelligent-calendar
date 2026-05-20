package com.example.calendar_service.service.impl;

import com.example.calendar_service.dto.*;
import com.example.calendar_service.entity.*;
import com.example.calendar_service.repository.*;
import com.example.calendar_service.service.CalendarService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.calendar_service.event.CalendarEventCreatedEvent;
import com.example.calendar_service.event.CalendarEventProducer;

@Service
public class CalendarServiceImpl implements CalendarService {
    private final EventRepository eventRepository;
    private final EventAttendeeRepository attendeeRepository;
    private final EventAttachmentRepository attachmentRepository;

    private final CalendarEventProducer calendarEventProducer;

    public CalendarServiceImpl(EventRepository eventRepository, EventAttendeeRepository attendeeRepository,
                               EventAttachmentRepository attachmentRepository,
                               CalendarEventProducer calendarEventProducer
    ) {
        this.eventRepository = eventRepository;
        this.attendeeRepository = attendeeRepository;
        this.attachmentRepository = attachmentRepository;
        this.calendarEventProducer = calendarEventProducer;
    }

    @Override
    @Transactional
    public EventResponse createEvent(Long familyId, Long userId, CreateEventRequest request) {
        if (!request.endTime().isAfter(request.startTime())) {
            throw new RuntimeException("End time must be after start time");
        }
        if (eventRepository.hasConflict(familyId, request.attendeeUserIds(), request.startTime(), request.endTime())) {
            throw new RuntimeException("Schedule conflict found for one or more attendees");
        }

        Event event = new Event();
        event.setFamilyId(familyId);
        event.setCreatedByUserId(userId);
        event.setTitle(request.title().trim());
        event.setDescription(request.description());
        event.setEventType(request.eventType());
        event.setVisibility(request.visibility());
        event.setLocation(request.location());
        event.setStartTime(request.startTime());
        event.setEndTime(request.endTime());
        event.setEstimatedBudget(request.estimatedBudget());
        event.setBudgetCategory(request.budgetCategory());
        event.setStatus("SCHEDULED");

        Event saved = eventRepository.save(event);

        request.attendeeUserIds().forEach(attendeeUserId -> {
            EventAttendee attendee = new EventAttendee();
            attendee.setEventId(saved.getId());
            attendee.setUserId(attendeeUserId);
            attendeeRepository.save(attendee);
        });
        CalendarEventCreatedEvent eventCreatedEvent = new CalendarEventCreatedEvent(
                familyId,
                saved.getId(),
                userId,
                saved.getTitle(),
                saved.getStartTime(),
                saved.getEndTime(),
                request.attendeeUserIds()
        );
        calendarEventProducer.publishEventCreated(eventCreatedEvent);

        return toResponse(saved, request.attendeeUserIds());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventResponse> getEvents(Long familyId, LocalDateTime from, LocalDateTime to) {
        List<Event> events = eventRepository
                .findByFamilyIdAndStartTimeLessThanAndEndTimeGreaterThanAndStatusNotOrderByStartTimeAsc(
                        familyId, to, from, "CANCELED");

        List<Long> eventIds = events.stream().map(Event::getId).toList();
        Map<Long, List<Long>> attendeeMap = attendeeRepository.findByEventIdIn(eventIds).stream()
                .collect(Collectors.groupingBy(
                        EventAttendee::getEventId,
                        Collectors.mapping(EventAttendee::getUserId, Collectors.toList())
                ));

        return events.stream()
                .map(event -> toResponse(event, attendeeMap.getOrDefault(event.getId(), List.of())))
                .toList();
    }

    @Override
    @Transactional
    public EventResponse completeEvent(Long familyId, Long eventId, Long userId, CompleteEventRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (!event.getFamilyId().equals(familyId)) {
            throw new RuntimeException("Event does not belong to this family");
        }

        event.setActualCost(request.actualCost());
        event.setStatus("COMPLETED");
        event.setCompletedAt(LocalDateTime.now());

        Event saved = eventRepository.save(event);
        List<Long> attendees = attendeeRepository.findByEventId(saved.getId()).stream()
                .map(EventAttendee::getUserId)
                .toList();

        return toResponse(saved, attendees);
    }

    @Override
    @Transactional
    public EventAttachmentResponse addAttachment(Long familyId, Long eventId, Long userId, EventAttachmentRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (!event.getFamilyId().equals(familyId)) {
            throw new RuntimeException("Event does not belong to this family");
        }

        EventAttachment attachment = new EventAttachment();
        attachment.setEventId(eventId);
        attachment.setUploadedByUserId(userId);
        attachment.setAttachmentType(request.attachmentType().trim().toUpperCase());
        attachment.setTitle(request.title().trim());
        attachment.setResourceUrl(request.resourceUrl().trim());
        attachment.setStage(request.stage().trim().toUpperCase());

        return toAttachmentResponse(attachmentRepository.save(attachment));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EventAttachmentResponse> getAttachments(Long familyId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        if (!event.getFamilyId().equals(familyId)) {
            throw new RuntimeException("Event does not belong to this family");
        }

        return attachmentRepository.findByEventIdOrderByCreatedAtDesc(eventId).stream()
                .map(this::toAttachmentResponse)
                .toList();
    }

    private EventResponse toResponse(Event event, List<Long> attendees) {
        return new EventResponse(event.getId(), event.getFamilyId(), event.getCreatedByUserId(),
                event.getTitle(), event.getDescription(), event.getEventType(), event.getVisibility(),
                event.getLocation(), event.getStartTime(), event.getEndTime(), event.getEstimatedBudget(),
                event.getActualCost(), event.getBudgetCategory(), event.getStatus(), event.getCompletedAt(), attendees);
    }

    private EventAttachmentResponse toAttachmentResponse(EventAttachment attachment) {
        return new EventAttachmentResponse(attachment.getId(), attachment.getEventId(), attachment.getUploadedByUserId(),
                attachment.getAttachmentType(), attachment.getTitle(), attachment.getResourceUrl(),
                attachment.getStage(), attachment.getCreatedAt());
    }
}
