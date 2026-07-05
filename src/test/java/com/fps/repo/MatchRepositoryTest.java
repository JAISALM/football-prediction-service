package com.fps.repo;

import com.fps.enums.MatchStatus;
import com.fps.model.Match;
import com.fps.FootballPredictionServiceApplication;
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
class MatchRepositoryTest {

    @Autowired
    private MatchRepository matchRepository;

    @Test
    void findByStatusReturnsMatches() {
        Match match1 = Match.builder()
                .homeTeam("Arsenal")
                .awayTeam("Chelsea")
                .status(MatchStatus.SCHEDULED)
                .build();
        Match match2 = Match.builder()
                .homeTeam("Liverpool")
                .awayTeam("ManCity")
                .status(MatchStatus.LIVE)
                .build();
        matchRepository.saveAll(List.of(match1, match2));

        List<Match> scheduled = matchRepository.findByStatus(MatchStatus.SCHEDULED);

        assertEquals(1, scheduled.size());
        assertEquals("Arsenal", scheduled.get(0).getHomeTeam());
    }

    @Test
    void findByStatusReturnsEmptyForNoMatches() {
        List<Match> finished = matchRepository.findByStatus(MatchStatus.FINISHED);

        assertTrue(finished.isEmpty());
    }

    @Test
    void findByStatusWithPagination() {
        for (int i = 0; i < 5; i++) {
            matchRepository.save(Match.builder()
                    .homeTeam("TeamA" + i)
                    .awayTeam("TeamB" + i)
                    .status(MatchStatus.SCHEDULED)
                    .build());
        }

        Page<Match> page = matchRepository.findByStatus(MatchStatus.SCHEDULED, PageRequest.of(0, 2));

        assertEquals(2, page.getContent().size());
        assertEquals(3, page.getTotalPages());
    }

    @Test
    void saveAndFindById() {
        Match saved = matchRepository.save(Match.builder()
                .homeTeam("Real Madrid")
                .awayTeam("Barcelona")
                .status(MatchStatus.LIVE)
                .currentMinute(45)
                .build());

        Match found = matchRepository.findById(saved.getId()).orElseThrow();

        assertEquals("Real Madrid", found.getHomeTeam());
        assertEquals(MatchStatus.LIVE, found.getStatus());
        assertEquals(45, found.getCurrentMinute());
        assertNotNull(found.getStartTime());
        assertNotNull(found.getCreatedAt());
    }

    @Test
    void defaultStatusIsScheduled() {
        Match saved = matchRepository.save(Match.builder()
                .homeTeam("Juventus")
                .awayTeam("AC Milan")
                .build());

        Match found = matchRepository.findById(saved.getId()).orElseThrow();
        assertEquals(MatchStatus.SCHEDULED, found.getStatus());
    }

    @Test
    void defaultCurrentMinuteIsZero() {
        Match saved = matchRepository.save(Match.builder()
                .homeTeam("PSG")
                .awayTeam("Lyon")
                .build());

        Match found = matchRepository.findById(saved.getId()).orElseThrow();
        assertEquals(0, found.getCurrentMinute());
    }
}
