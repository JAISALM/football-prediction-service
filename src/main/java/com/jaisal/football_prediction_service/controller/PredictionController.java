package com.jaisal.football_prediction_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PredictionController {

	@GetMapping("/ping")
	public String ping() {
		return "pong";
	}

	@GetMapping("/test")
	public ResponseEntity<Map<String, String>> test() {
		return ResponseEntity.ok(Map.of(
				"status", "ok",
				"message", "Application is running and working as expected"
		));
	}

}
