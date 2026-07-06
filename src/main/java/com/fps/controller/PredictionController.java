package com.fps.controller;

import com.fps.dto.PredictionRequest;
import com.fps.dto.PredictionResponse;
import com.fps.service.PredictionService;
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
            @RequestHeader("X-User-Id") String userId,
            @RequestBody PredictionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(predictionService.submitPrediction(userId, request));
    }
}
