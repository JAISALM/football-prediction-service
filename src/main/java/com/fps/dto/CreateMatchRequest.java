package com.fps.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public record CreateMatchRequest(
        @NotBlank String homeTeam,
        @NotBlank String awayTeam,
        LocalDateTime startTime
) {}
