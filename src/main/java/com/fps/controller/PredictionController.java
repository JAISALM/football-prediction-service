package com.fps.controller;

import com.fps.dto.PredictionRequest;
import com.fps.dto.PredictionResponse;
import com.fps.service.PredictionService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/predictions")
public class PredictionController {

    private final PredictionService predictionService;

    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @PostMapping
    public ResponseEntity<PredictionResponse> submitPrediction(
            @RequestHeader(value = "X-User-Id", required = true)
            @NotBlank String userId,
            @Valid @RequestBody PredictionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(predictionService.submitPrediction(userId, request));
    }
}
