package com.fps.dto;

import com.fps.enums.QuestionType;
import com.fps.enums.WindowStatus;

public record WindowResponse(
        String id,
        Integer windowIndex,
        Integer startMinute,
        Integer endMinute,
        QuestionType questionType,
        WindowStatus status,
        Boolean resultValue
) {}
