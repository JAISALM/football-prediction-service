package com.fps.controller;

import com.fps.dto.PredictionResponse;
import com.fps.service.PredictionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("local")
class PredictionControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @MockitoBean
    private PredictionService predictionService;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Test
    void submitPredictionWithValidRequestReturns201() throws Exception {
        PredictionResponse resp = new PredictionResponse("p1", "w1", true, null);
        when(predictionService.submitPrediction(eq("u1"), any())).thenReturn(resp);

        String body = """
                {"windowId": "w1", "predictedValue": true}
                """;

        mockMvc.perform(post("/api/predictions")
                        .header("X-User-Id", "u1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("p1"))
                .andExpect(jsonPath("$.windowId").value("w1"));
    }

    @Test
    void submitPredictionWithoutUserIdHeaderReturns400() throws Exception {
        String body = """
                {"windowId": "w1", "predictedValue": true}
                """;

        mockMvc.perform(post("/api/predictions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void submitPredictionWithBlankWindowIdReturns400() throws Exception {
        String body = """
                {"windowId": "", "predictedValue": true}
                """;

        mockMvc.perform(post("/api/predictions")
                        .header("X-User-Id", "u1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void submitPredictionWithNullPredictedValueReturns400() throws Exception {
        String body = """
                {"windowId": "w1"}
                """;

        mockMvc.perform(post("/api/predictions")
                        .header("X-User-Id", "u1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
