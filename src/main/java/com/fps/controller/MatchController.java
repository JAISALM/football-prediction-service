package com.fps.controller;

import com.fps.dto.CreateMatchRequest;
import com.fps.dto.MatchResponse;
import com.fps.enums.MatchStatus;
import com.fps.service.MatchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping
    public ResponseEntity<List<MatchResponse>> getAllMatches() {
        return ResponseEntity.ok(matchService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchResponse> getMatchById(@PathVariable String id) {
        return ResponseEntity.ok(matchService.findById(id));
    }

    @GetMapping("/by-status/{status}")
    public ResponseEntity<List<MatchResponse>> getMatchesByStatus(@PathVariable MatchStatus status) {
        return ResponseEntity.ok(matchService.findByStatus(status));
    }

    @PostMapping
    public ResponseEntity<MatchResponse> createMatch(@RequestBody CreateMatchRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(matchService.createMatch(request));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<MatchResponse> startMatch(@PathVariable String id) {
        return ResponseEntity.ok(matchService.startMatch(id));
    }
}
