package com.fps.service;

import com.fps.dto.CreateMatchRequest;
import com.fps.dto.MatchResponse;
import com.fps.entities.Match;
import com.fps.entities.PredictionWindow;
import com.fps.enums.MatchStatus;
import com.fps.enums.QuestionType;
import com.fps.exception.ResourceNotFoundException;
import com.fps.repo.MatchRepository;
import com.fps.repo.PredictionWindowRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MatchService {

    private final MatchRepository matchRepository;
    private final PredictionWindowRepository windowRepository;

    public MatchService(MatchRepository matchRepository, PredictionWindowRepository windowRepository) {
        this.matchRepository = matchRepository;
        this.windowRepository = windowRepository;
    }

    public List<MatchResponse> findAll() {
        return matchRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public MatchResponse findById(String id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with id: " + id));
        return toResponse(match);
    }

    public List<MatchResponse> findByStatus(MatchStatus status) {
        return matchRepository.findByStatus(status).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public MatchResponse createMatch(CreateMatchRequest request) {
        Match match = Match.builder()
                .homeTeam(request.homeTeam())
                .awayTeam(request.awayTeam())
                .status(MatchStatus.SCHEDULED)
                .currentMinute(0)
                .startTime(request.startTime() != null ? request.startTime() : LocalDateTime.now())
                .build();

        match = matchRepository.save(match);
        generatePredictionWindows(match);
        return toResponse(match);
    }

    @Transactional
    public MatchResponse startMatch(String id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with id: " + id));

        match.setStatus(MatchStatus.LIVE);
        match.setCurrentMinute(0);
        match = matchRepository.save(match);
        return toResponse(match);
    }

    void generatePredictionWindows(Match match) {
        QuestionType[] types = QuestionType.values();
        for (int i = 0; i < 18; i++) {
            int startMinute = i * 5;
            int endMinute = startMinute + 5;

            PredictionWindow window = PredictionWindow.builder()
                    .match(match)
                    .windowIndex(i)
                    .startMinute(startMinute)
                    .endMinute(endMinute)
                    .questionType(types[i % types.length])
                    .build();

            windowRepository.save(window);
        }
    }

    MatchResponse toResponse(Match match) {
        return new MatchResponse(
                match.getId(),
                match.getHomeTeam(),
                match.getAwayTeam(),
                match.getStatus(),
                match.getCurrentMinute(),
                match.getStartTime()
        );
    }
}
