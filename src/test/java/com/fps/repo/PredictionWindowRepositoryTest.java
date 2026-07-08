package com.fps.repo;

import com.fps.enums.MatchStatus;
import com.fps.enums.QuestionType;
import com.fps.enums.WindowStatus;
import com.fps.entities.Match;
import com.fps.entities.PredictionWindow;
import com.fps.FootballPredictionServiceApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = FootballPredictionServiceApplication.class)
@ActiveProfiles("local")
@Transactional
class PredictionWindowRepositoryTest {

    @Autowired
    private PredictionWindowRepository predictionWindowRepository;

    @Autowired
    private MatchRepository matchRepository;

    @BeforeEach
    void setUp() {
        predictionWindowRepository.deleteAll();
        matchRepository.deleteAll();
    }

    @Test
    void findByMatchIdAndStatusReturnsWindows() {
        Match match = matchRepository.save(Match.builder()
                .homeTeam("Arsenal")
                .awayTeam("Chelsea")
                .status(MatchStatus.LIVE)
                .build());

        predictionWindowRepository.saveAll(List.of(
                PredictionWindow.builder()
                        .match(match)
                        .windowIndex(1)
                        .startMinute(0)
                        .endMinute(15)
                        .questionType(QuestionType.GOAL)
                        .status(WindowStatus.OPEN)
                        .build(),
                PredictionWindow.builder()
                        .match(match)
                        .windowIndex(2)
                        .startMinute(15)
                        .endMinute(30)
                        .questionType(QuestionType.GOAL)
                        .status(WindowStatus.LOCKED)
                        .build()
        ));

        Page<PredictionWindow> open = predictionWindowRepository.findByMatchIdAndStatus(
                match.getId(), WindowStatus.OPEN, PageRequest.of(0, 10));

        assertEquals(1, open.getContent().size());
        assertEquals(1, open.getContent().get(0).getWindowIndex());
    }

    @Test
    void findByMatchIdAndStatusWithPaginationAndEntityGraph() {
        Match match = matchRepository.save(Match.builder()
                .homeTeam("Liverpool")
                .awayTeam("ManCity")
                .status(MatchStatus.LIVE)
                .build());

        for (int i = 0; i < 5; i++) {
            predictionWindowRepository.save(PredictionWindow.builder()
                    .match(match)
                    .windowIndex(i)
                    .startMinute(i * 10)
                    .endMinute(i * 10 + 10)
                    .questionType(QuestionType.CORNER)
                    .status(WindowStatus.OPEN)
                    .build());
        }

        Page<PredictionWindow> page = predictionWindowRepository.findByMatchIdAndStatus(
                match.getId(), WindowStatus.OPEN, PageRequest.of(0, 2));

        assertEquals(2, page.getContent().size());
        assertEquals(3, page.getTotalPages());
        assertNotNull(page.getContent().get(0).getMatch());
    }

    @Test
    void defaultStatusIsOpen() {
        Match match = matchRepository.save(Match.builder()
                .homeTeam("Roma")
                .awayTeam("Napoli")
                .build());

        PredictionWindow saved = predictionWindowRepository.save(PredictionWindow.builder()
                .match(match)
                .windowIndex(1)
                .startMinute(0)
                .endMinute(15)
                .questionType(QuestionType.CARD)
                .build());

        assertEquals(WindowStatus.OPEN, saved.getStatus());
    }

    @Test
    void findByMatchIdReturnsEmptyPageForUnknownMatch() {
        Page<PredictionWindow> windows = predictionWindowRepository.findByMatchIdAndStatus(
                "non-existent-id", WindowStatus.OPEN, PageRequest.of(0, 10));

        assertTrue(windows.getContent().isEmpty());
    }

    @Test
    void saveWithResultValue() {
        Match match = matchRepository.save(Match.builder()
                .homeTeam("Ajax")
                .awayTeam("Feyenoord")
                .build());

        PredictionWindow saved = predictionWindowRepository.save(PredictionWindow.builder()
                .match(match)
                .windowIndex(1)
                .startMinute(0)
                .endMinute(15)
                .questionType(QuestionType.GOAL)
                .status(WindowStatus.RESOLVED)
                .resultValue(true)
                .build());

        PredictionWindow found = predictionWindowRepository.findById(saved.getId()).orElseThrow();
        assertTrue(found.getResultValue());
        assertEquals(WindowStatus.RESOLVED, found.getStatus());
    }

    @Test
    void versionFieldExists() {
        Match match = matchRepository.save(Match.builder()
                .homeTeam("version_match")
                .awayTeam("version_opponent")
                .build());

        PredictionWindow saved = predictionWindowRepository.save(PredictionWindow.builder()
                .match(match)
                .windowIndex(1)
                .startMinute(0)
                .endMinute(5)
                .questionType(QuestionType.GOAL)
                .build());

        PredictionWindow found = predictionWindowRepository.findById(saved.getId()).orElseThrow();
        assertNotNull(found.getVersion());
    }
}
