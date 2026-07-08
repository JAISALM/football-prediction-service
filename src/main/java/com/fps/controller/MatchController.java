package com.fps.controller;

import com.fps.dto.CreateMatchRequest;
import com.fps.dto.MatchResponse;
import com.fps.enums.MatchStatus;
import com.fps.service.MatchService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @GetMapping
    public ResponseEntity<Page<MatchResponse>> getAllMatches(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(matchService.findAll(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MatchResponse> getMatchById(@PathVariable String id) {
        return ResponseEntity.ok(matchService.findById(id));
    }

    @GetMapping("/by-status/{status}")
    public ResponseEntity<Page<MatchResponse>> getMatchesByStatus(
            @PathVariable MatchStatus status,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(matchService.findByStatus(status, pageable));
    }

    @PostMapping
    public ResponseEntity<MatchResponse> createMatch(
            @Valid @RequestBody CreateMatchRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(matchService.createMatch(request));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<MatchResponse> startMatch(@PathVariable String id) {
        return ResponseEntity.ok(matchService.startMatch(id));
    }
}
