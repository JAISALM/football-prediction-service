package com.fps.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PredictionRequest(
        @NotBlank String windowId,
        @NotNull Boolean predictedValue
) {}
