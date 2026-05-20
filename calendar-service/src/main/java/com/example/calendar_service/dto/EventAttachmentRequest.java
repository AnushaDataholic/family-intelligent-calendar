package com.example.calendar_service.dto;

import jakarta.validation.constraints.NotBlank;

public record EventAttachmentRequest(
        @NotBlank String attachmentType,
        @NotBlank String title,
        @NotBlank String resourceUrl,
        @NotBlank String stage
) {}
