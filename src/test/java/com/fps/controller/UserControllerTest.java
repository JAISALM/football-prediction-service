package com.fps.controller;

import com.fps.dto.UserCreateResponse;
import com.fps.dto.UserStatsResponse;
import com.fps.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("local")
class UserControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void getUserStatsReturnsStats() throws Exception {
        UserStatsResponse resp = new UserStatsResponse("u1", "player1", 150, 5, 3, List.of());
        when(userService.getUserStats("u1")).thenReturn(resp);

        mockMvc.perform(get("/api/users/u1/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value("u1"))
                .andExpect(jsonPath("$.totalPoints").value(150))
                .andExpect(jsonPath("$.totalPredictions").value(5));
    }

    @Test
    void createUserWithValidRequestReturns201() throws Exception {
        UserCreateResponse resp = new UserCreateResponse("u1", "player1", 100);
        when(userService.createUser("player1")).thenReturn(resp);

        String body = """
                {"username": "player1"}
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("u1"))
                .andExpect(jsonPath("$.totalPoints").value(100));
    }

    @Test
    void createUserWithBlankUsernameReturns400() throws Exception {
        String body = """
                {"username": ""}
                """;

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
