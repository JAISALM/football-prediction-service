package com.fps.service;

import com.fps.dto.PredictionRequest;
import com.fps.dto.PredictionResponse;
import com.fps.entities.Match;
import com.fps.entities.PredictionWindow;
import com.fps.entities.User;
import com.fps.entities.UserPrediction;
import com.fps.enums.MatchStatus;
import com.fps.enums.QuestionType;
import com.fps.enums.WindowStatus;
import com.fps.exception.DuplicatePredictionException;
import com.fps.exception.ResourceNotFoundException;
import com.fps.exception.WindowClosedException;
import com.fps.repo.PredictionWindowRepository;
import com.fps.repo.UserPredictionRepository;
import com.fps.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PredictionServiceUnitTest {

    @Mock
    private UserPredictionRepository userPredictionRepository;

    @Mock
    private PredictionWindowRepository windowRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PredictionService predictionService;

    User createSavedUser() {
        return User.builder()
                .id("u1")
                .username("player1")
                .totalPoints(100)
                .build();
    }

    PredictionWindow createOpenWindow() {
        return PredictionWindow.builder()
                .id("w1")
                .match(Match.builder().id("m1").build())
                .windowIndex(1)
                .startMinute(0)
                .endMinute(5)
                .questionType(QuestionType.GOAL)
                .status(WindowStatus.OPEN)
                .build();
    }

    UserPrediction createSavedPrediction() {
        return UserPrediction.builder()
                .id("p1")
                .user(createSavedUser())
                .window(createOpenWindow())
                .predictedValue(true)
                .build();
    }

    @Test
    void submitPredictionSuccess() {
        User user = createSavedUser();
        PredictionWindow window = createOpenWindow();
        UserPrediction saved = createSavedPrediction();

        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(windowRepository.findById("w1")).thenReturn(Optional.of(window));
        when(userPredictionRepository.findByUserIdAndWindowId("u1", "w1")).thenReturn(Optional.empty());
        when(userPredictionRepository.save(any(UserPrediction.class))).thenReturn(saved);

        PredictionRequest request = new PredictionRequest("w1", true);
        PredictionResponse result = predictionService.submitPrediction("u1", request);

        assertEquals("p1", result.id());
        assertEquals("w1", result.windowId());
        assertTrue(result.predictedValue());
        verify(userPredictionRepository).save(any(UserPrediction.class));
    }

    @Test
    void submitPredictionThrowsWhenUserNotFound() {
        when(userRepository.findById("missing")).thenReturn(Optional.empty());

        PredictionRequest request = new PredictionRequest("w1", true);

        assertThrows(ResourceNotFoundException.class,
                () -> predictionService.submitPrediction("missing", request));
    }

    @Test
    void submitPredictionThrowsWhenWindowNotFound() {
        when(userRepository.findById("u1")).thenReturn(Optional.of(createSavedUser()));
        when(windowRepository.findById("missing")).thenReturn(Optional.empty());

        PredictionRequest request = new PredictionRequest("missing", true);

        assertThrows(ResourceNotFoundException.class,
                () -> predictionService.submitPrediction("u1", request));
    }

    @Test
    void submitPredictionThrowsDuplicateException() {
        when(userRepository.findById("u1")).thenReturn(Optional.of(createSavedUser()));
        when(windowRepository.findById("w1")).thenReturn(Optional.of(createOpenWindow()));
        when(userPredictionRepository.findByUserIdAndWindowId("u1", "w1")).thenReturn(Optional.of(createSavedPrediction()));

        PredictionRequest request = new PredictionRequest("w1", true);

        assertThrows(DuplicatePredictionException.class,
                () -> predictionService.submitPrediction("u1", request));
    }

    @Test
    void submitPredictionThrowsWhenWindowClosed() {
        PredictionWindow closedWindow = createOpenWindow();
        closedWindow.setStatus(WindowStatus.LOCKED);

        when(userRepository.findById("u1")).thenReturn(Optional.of(createSavedUser()));
        when(windowRepository.findById("w1")).thenReturn(Optional.of(closedWindow));
        when(userPredictionRepository.findByUserIdAndWindowId("u1", "w1")).thenReturn(Optional.empty());

        PredictionRequest request = new PredictionRequest("w1", true);

        assertThrows(WindowClosedException.class,
                () -> predictionService.submitPrediction("u1", request));
    }

    @Test
    void submitPredictionWithFalseValue() {
        User user = createSavedUser();
        UserPrediction saved = createSavedPrediction();
        saved.setPredictedValue(false);

        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(windowRepository.findById("w1")).thenReturn(Optional.of(createOpenWindow()));
        when(userPredictionRepository.findByUserIdAndWindowId("u1", "w1")).thenReturn(Optional.empty());
        when(userPredictionRepository.save(any(UserPrediction.class))).thenReturn(saved);

        PredictionRequest request = new PredictionRequest("w1", false);
        PredictionResponse result = predictionService.submitPrediction("u1", request);

        assertFalse(result.predictedValue());
    }
}
