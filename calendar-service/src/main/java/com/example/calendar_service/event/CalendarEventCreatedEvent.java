package com.example.calendar_service.event;

import java.time.LocalDateTime;
import java.util.List;

public record CalendarEventCreatedEvent(
        Long familyId,
        Long eventId,
        Long createdByUserId,
        String title,
        LocalDateTime startTime,
        LocalDateTime endTime,
        List<Long> attendeeUserIds
) {}
