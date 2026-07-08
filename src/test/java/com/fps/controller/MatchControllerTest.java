package com.fps.controller;

import com.fps.dto.CreateMatchRequest;
import com.fps.dto.MatchResponse;
import com.fps.enums.MatchStatus;
import com.fps.service.MatchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("local")
class MatchControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockitoBean
    private MatchService matchService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void getAllMatchesReturnsPagedResults() throws Exception {
        MatchResponse resp = new MatchResponse("m1", "Arsenal", "Chelsea",
                MatchStatus.SCHEDULED, 0, LocalDateTime.now());
        when(matchService.findAll(any())).thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(resp)));

        mockMvc.perform(get("/api/matches"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value("m1"))
                .andExpect(jsonPath("$.content[0].homeTeam").value("Arsenal"));
    }

    @Test
    void getMatchByIdReturnsMatch() throws Exception {
        MatchResponse resp = new MatchResponse("m1", "Arsenal", "Chelsea",
                MatchStatus.SCHEDULED, 0, LocalDateTime.now());
        when(matchService.findById("m1")).thenReturn(resp);

        mockMvc.perform(get("/api/matches/m1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("m1"));
    }

    @Test
    void getMatchesByStatusReturnsPagedMatches() throws Exception {
        MatchResponse resp = new MatchResponse("m1", "Arsenal", "Chelsea",
                MatchStatus.LIVE, 10, LocalDateTime.now());
        when(matchService.findByStatus(eq(MatchStatus.LIVE), any()))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(resp)));

        mockMvc.perform(get("/api/matches/by-status/LIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value("LIVE"));
    }

    @Test
    void createMatchWithValidRequestReturns201() throws Exception {
        MatchResponse resp = new MatchResponse("m1", "Arsenal", "Chelsea",
                MatchStatus.SCHEDULED, 0, LocalDateTime.now());
        when(matchService.createMatch(any())).thenReturn(resp);

        String body = """
                {"homeTeam": "Arsenal", "awayTeam": "Chelsea"}
                """;

        mockMvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("m1"));
    }

    @Test
    void createMatchWithBlankHomeTeamReturns400() throws Exception {
        String body = """
                {"homeTeam": "", "awayTeam": "Chelsea"}
                """;

        mockMvc.perform(post("/api/matches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void startMatchReturnsUpdatedMatch() throws Exception {
        MatchResponse resp = new MatchResponse("m1", "Arsenal", "Chelsea",
                MatchStatus.LIVE, 0, LocalDateTime.now());
        when(matchService.startMatch("m1")).thenReturn(resp);

        mockMvc.perform(post("/api/matches/m1/start"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("LIVE"));
    }
}
