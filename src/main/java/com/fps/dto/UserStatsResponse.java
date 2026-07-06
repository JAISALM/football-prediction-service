package com.fps.dto;

import java.util.List;

public record UserStatsResponse(
        String userId,
        String username,
        Integer totalPoints,
        Integer totalPredictions,
        Integer correctPredictions,
        List<PredictionResponse> recentPredictions
) {}
