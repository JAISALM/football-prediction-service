package com.fps.dto;

public record UserCreateResponse(
        String id,
        String username,
        Integer totalPoints
) {}
