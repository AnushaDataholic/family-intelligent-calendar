package com.example.calendar_service.controller;

import com.example.calendar_service.dto.*;
import com.example.calendar_service.service.CalendarService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/families/{familyId}/events")
public class CalendarController {
    private final CalendarService calendarService;

    public CalendarController(CalendarService calendarService) {
        this.calendarService = calendarService;
    }

    @PostMapping
    public EventResponse createEvent(@PathVariable Long familyId, @RequestHeader("X-User-Id") Long userId,
                                     @Valid @RequestBody CreateEventRequest request) {
        return calendarService.createEvent(familyId, userId, request);
    }

    @GetMapping
    public List<EventResponse> getEvents(@PathVariable Long familyId, @RequestParam LocalDateTime from,
                                         @RequestParam LocalDateTime to) {
        return calendarService.getEvents(familyId, from, to);
    }

    @PatchMapping("/{eventId}/complete")
    public EventResponse completeEvent(@PathVariable Long familyId, @PathVariable Long eventId,
                                       @RequestHeader("X-User-Id") Long userId,
                                       @Valid @RequestBody CompleteEventRequest request) {
        return calendarService.completeEvent(familyId, eventId, userId, request);
    }

    @PostMapping("/{eventId}/attachments")
    public EventAttachmentResponse addAttachment(@PathVariable Long familyId, @PathVariable Long eventId,
                                                 @RequestHeader("X-User-Id") Long userId,
                                                 @Valid @RequestBody EventAttachmentRequest request) {
        return calendarService.addAttachment(familyId, eventId, userId, request);
    }

    @GetMapping("/{eventId}/attachments")
    public List<EventAttachmentResponse> getAttachments(@PathVariable Long familyId, @PathVariable Long eventId) {
        return calendarService.getAttachments(familyId, eventId);
    }
}
