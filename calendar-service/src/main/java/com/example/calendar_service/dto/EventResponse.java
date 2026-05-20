package com.example.calendar_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record EventResponse(
        Long id,
        Long familyId,
        Long createdByUserId,
        String title,
        String description,
        String eventType,
        String visibility,
        String location,
        LocalDateTime startTime,
        LocalDateTime endTime,
        BigDecimal estimatedBudget,
        BigDecimal actualCost,
        String budgetCategory,
        String status,
        LocalDateTime completedAt,
        List<Long> attendeeUserIds
) {}
