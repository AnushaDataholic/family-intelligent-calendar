package com.example.calendar_service.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CompleteEventRequest(@NotNull BigDecimal actualCost) {}
