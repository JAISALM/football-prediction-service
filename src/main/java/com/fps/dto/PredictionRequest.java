package com.fps.dto;

public record PredictionRequest(
        String windowId,
        Boolean predictedValue
) {}
