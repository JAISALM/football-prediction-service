package com.fps.service;

import com.fps.dto.CreateMatchRequest;
import com.fps.dto.MatchResponse;
import com.fps.entities.Match;
import com.fps.enums.MatchStatus;
import com.fps.exception.ResourceNotFoundException;
import com.fps.repo.MatchRepository;
import com.fps.repo.PredictionWindowRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchServiceUnitTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private PredictionWindowRepository windowRepository;

    @InjectMocks
    private MatchService matchService;

    Match createSavedMatch() {
        return Match.builder()
                .id("m1")
                .homeTeam("Arsenal")
                .awayTeam("Chelsea")
                .status(MatchStatus.SCHEDULED)
                .currentMinute(0)
                .startTime(LocalDateTime.now())
                .build();
    }

    @Test
    void findAllReturnsPagedResults() {
        Match match = createSavedMatch();
        Page<Match> page = new PageImpl<>(List.of(match));
        when(matchRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<MatchResponse> result = matchService.findAll(PageRequest.of(0, 10));

        assertEquals(1, result.getContent().size());
        assertEquals("m1", result.getContent().get(0).id());
        verify(matchRepository).findAll(any(Pageable.class));
    }

    @Test
    void findByIdReturnsMatch() {
        Match match = createSavedMatch();
        when(matchRepository.findById("m1")).thenReturn(Optional.of(match));

        MatchResponse result = matchService.findById("m1");

        assertEquals("m1", result.id());
        assertEquals("Arsenal", result.homeTeam());
    }

    @Test
    void findByIdThrowsWhenNotFound() {
        when(matchRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> matchService.findById("missing"));
    }

    @Test
    void findByStatusReturnsPagedMatches() {
        Match match = createSavedMatch();
        Page<Match> page = new PageImpl<>(List.of(match));
        when(matchRepository.findByStatus(eq(MatchStatus.SCHEDULED), any(Pageable.class))).thenReturn(page);

        Page<MatchResponse> result = matchService.findByStatus(MatchStatus.SCHEDULED, PageRequest.of(0, 10));

        assertEquals(1, result.getContent().size());
    }

    @Test
    void createMatchSavesAndGeneratesWindows() {
        Match saved = createSavedMatch();
        when(matchRepository.save(any(Match.class))).thenReturn(saved);

        CreateMatchRequest request = new CreateMatchRequest("Arsenal", "Chelsea", null);
        MatchResponse result = matchService.createMatch(request);

        assertEquals("m1", result.id());
        verify(windowRepository).saveAll(anyList());
    }

    @Test
    void createMatchUsesProvidedStartTime() {
        Match saved = createSavedMatch();
        when(matchRepository.save(any(Match.class))).thenReturn(saved);

        LocalDateTime specificTime = LocalDateTime.of(2026, 8, 1, 20, 0);
        CreateMatchRequest request = new CreateMatchRequest("Arsenal", "Chelsea", specificTime);
        MatchResponse result = matchService.createMatch(request);

        assertNotNull(result);
        verify(matchRepository).save(any(Match.class));
    }

    @Test
    void startMatchUpdatesStatus() {
        Match match = createSavedMatch();
        when(matchRepository.findById("m1")).thenReturn(Optional.of(match));
        when(matchRepository.save(any(Match.class))).thenAnswer(i -> i.getArgument(0));

        MatchResponse result = matchService.startMatch("m1");

        assertEquals(MatchStatus.LIVE, result.status());
        assertEquals(0, result.currentMinute());
    }

    @Test
    void startMatchThrowsWhenNotFound() {
        when(matchRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> matchService.startMatch("missing"));
    }
}
