package com.fps.dto;

import com.fps.enums.MatchStatus;
import java.time.LocalDateTime;

public record MatchResponse(
        String id,
        String homeTeam,
        String awayTeam,
        MatchStatus status,
        Integer currentMinute,
        LocalDateTime startTime
) {}
