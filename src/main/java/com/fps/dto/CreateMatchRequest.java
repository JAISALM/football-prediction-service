package com.fps.dto;

public record CreateMatchRequest(
        String homeTeam,
        String awayTeam,
        java.time.LocalDateTime startTime
) {}
