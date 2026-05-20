package com.example.calendar_service.dto;

import java.time.LocalDateTime;

public record EventAttachmentResponse(
        Long id,
        Long eventId,
        Long uploadedByUserId,
        String attachmentType,
        String title,
        String resourceUrl,
        String stage,
        LocalDateTime createdAt
) {}
