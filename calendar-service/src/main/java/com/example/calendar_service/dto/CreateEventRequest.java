package com.example.calendar_service.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CreateEventRequest(
        @NotBlank String title,
        String description,
        String eventType,
        String visibility,
        String location,
        @NotNull LocalDateTime startTime,
        @NotNull LocalDateTime endTime,
        BigDecimal estimatedBudget,
        String budgetCategory,
        @NotEmpty List<Long> attendeeUserIds
) {}
