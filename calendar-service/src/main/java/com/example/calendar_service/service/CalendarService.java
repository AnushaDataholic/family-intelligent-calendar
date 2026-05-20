package com.example.calendar_service.service;

import com.example.calendar_service.dto.*;
import java.time.LocalDateTime;
import java.util.List;

public interface CalendarService {
    EventResponse createEvent(Long familyId, Long userId, CreateEventRequest request);
    List<EventResponse> getEvents(Long familyId, LocalDateTime from, LocalDateTime to);
    EventResponse completeEvent(Long familyId, Long eventId, Long userId, CompleteEventRequest request);
    EventAttachmentResponse addAttachment(Long familyId, Long eventId, Long userId, EventAttachmentRequest request);
    List<EventAttachmentResponse> getAttachments(Long familyId, Long eventId);
}
