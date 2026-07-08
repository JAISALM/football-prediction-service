package com.fps.dto;

public record PredictionResponse(
        String id,
        String windowId,
        Boolean predictedValue,
        Integer pointsEarned
) {}
