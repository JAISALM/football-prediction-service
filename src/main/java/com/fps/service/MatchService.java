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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Slf4j
@Service
public class MatchService {

    static final int TOTAL_PREDICTION_WINDOWS = 18;
    static final int WINDOW_MINUTES = 5;

    private final MatchRepository matchRepository;
    private final PredictionWindowRepository windowRepository;

    public MatchService(MatchRepository matchRepository, PredictionWindowRepository windowRepository) {
        this.matchRepository = matchRepository;
        this.windowRepository = windowRepository;
    }

    @Transactional(readOnly = true)
    public Page<MatchResponse> findAll(Pageable pageable) {
        return matchRepository.findAll(pageable).map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public MatchResponse findById(String id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match", id));
        return toResponse(match);
    }

    @Transactional(readOnly = true)
    public Page<MatchResponse> findByStatus(MatchStatus status, Pageable pageable) {
        return matchRepository.findByStatus(status, pageable).map(this::toResponse);
    }

    @Transactional
    public MatchResponse createMatch(CreateMatchRequest request) {
        Match match = Match.builder()
                .homeTeam(request.homeTeam())
                .awayTeam(request.awayTeam())
                .status(MatchStatus.SCHEDULED)
                .currentMinute(0)
                .startTime(request.startTime() != null ? request.startTime() : LocalDateTime.now(ZoneOffset.UTC))
                .build();

        match = matchRepository.save(match);
        log.info("Created match {} ({} vs {})", match.getId(), match.getHomeTeam(), match.getAwayTeam());
        generatePredictionWindows(match);
        return toResponse(match);
    }

    @Transactional
    public MatchResponse startMatch(String id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Match", id));

        match.setStatus(MatchStatus.LIVE);
        match.setCurrentMinute(0);
        match = matchRepository.save(match);
        log.info("Started match {}", match.getId());
        return toResponse(match);
    }

    void generatePredictionWindows(Match match) {
        QuestionType[] types = QuestionType.values();
        List<PredictionWindow> windows = new java.util.ArrayList<>(TOTAL_PREDICTION_WINDOWS);
        for (int i = 0; i < TOTAL_PREDICTION_WINDOWS; i++) {
            int startMinute = i * WINDOW_MINUTES;
            int endMinute = startMinute + WINDOW_MINUTES;

            PredictionWindow window = PredictionWindow.builder()
                    .match(match)
                    .windowIndex(i)
                    .startMinute(startMinute)
                    .endMinute(endMinute)
                    .questionType(types[i % types.length])
                    .build();

            windows.add(window);
        }
        windowRepository.saveAll(windows);
        log.info("Generated {} prediction windows for match {}", TOTAL_PREDICTION_WINDOWS, match.getId());
    }

    private MatchResponse toResponse(Match match) {
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
