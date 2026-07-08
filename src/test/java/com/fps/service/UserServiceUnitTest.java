package com.fps.service;

import com.fps.dto.PredictionResponse;
import com.fps.dto.UserCreateResponse;
import com.fps.dto.UserStatsResponse;
import com.fps.entities.PredictionWindow;
import com.fps.entities.User;
import com.fps.entities.UserPrediction;
import com.fps.enums.QuestionType;
import com.fps.enums.WindowStatus;
import com.fps.exception.ResourceNotFoundException;
import com.fps.repo.UserPredictionRepository;
import com.fps.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserPredictionRepository userPredictionRepository;

    @InjectMocks
    private UserService userService;

    User createSavedUser() {
        return User.builder()
                .id("u1")
                .username("player1")
                .totalPoints(150)
                .build();
    }

    UserPrediction createPrediction(String predId, String windowId, Integer points) {
        PredictionWindow window = PredictionWindow.builder()
                .id(windowId)
                .windowIndex(1)
                .startMinute(0)
                .endMinute(5)
                .questionType(QuestionType.GOAL)
                .status(WindowStatus.OPEN)
                .build();
        return UserPrediction.builder()
                .id(predId)
                .user(createSavedUser())
                .window(window)
                .predictedValue(true)
                .pointsEarned(points)
                .build();
    }

    @Test
    void getUserStatsReturnsCorrectStats() {
        User user = createSavedUser();
        List<UserPrediction> predictions = List.of(
                createPrediction("p1", "w1", 10),
                createPrediction("p2", "w2", 0),
                createPrediction("p3", "w3", 5)
        );
        Page<UserPrediction> page = new PageImpl<>(predictions);

        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(userPredictionRepository.countByUserId("u1")).thenReturn(5L);
        when(userPredictionRepository.countByUserIdAndPointsEarnedGreaterThan("u1", 0)).thenReturn(3L);
        when(userPredictionRepository.findByUserId(eq("u1"), any(PageRequest.class))).thenReturn(page);

        UserStatsResponse result = userService.getUserStats("u1");

        assertEquals("u1", result.userId());
        assertEquals("player1", result.username());
        assertEquals(150, result.totalPoints());
        assertEquals(5, result.totalPredictions());
        assertEquals(3, result.correctPredictions());
        assertEquals(3, result.recentPredictions().size());
    }

    @Test
    void getUserStatsWithNoPredictions() {
        User user = createSavedUser();
        Page<UserPrediction> emptyPage = new PageImpl<>(List.of());

        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(userPredictionRepository.countByUserId("u1")).thenReturn(0L);
        when(userPredictionRepository.countByUserIdAndPointsEarnedGreaterThan("u1", 0)).thenReturn(0L);
        when(userPredictionRepository.findByUserId(eq("u1"), any(PageRequest.class))).thenReturn(emptyPage);

        UserStatsResponse result = userService.getUserStats("u1");

        assertEquals(0, result.totalPredictions());
        assertEquals(0, result.correctPredictions());
        assertTrue(result.recentPredictions().isEmpty());
    }

    @Test
    void getUserStatsThrowsWhenUserNotFound() {
        when(userRepository.findById("missing")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserStats("missing"));
    }

    @Test
    void createUserReturnsDto() {
        User saved = User.builder()
                .id("u1")
                .username("player1")
                .totalPoints(UserService.DEFAULT_USER_POINTS)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(saved);

        UserCreateResponse result = userService.createUser("player1");

        assertEquals("u1", result.id());
        assertEquals("player1", result.username());
        assertEquals(UserService.DEFAULT_USER_POINTS, result.totalPoints());
    }

    @Test
    void getUserStatsUsesAggregateQueriesNotListSize() {
        User user = createSavedUser();

        when(userRepository.findById("u1")).thenReturn(Optional.of(user));
        when(userPredictionRepository.countByUserId("u1")).thenReturn(100L);
        when(userPredictionRepository.countByUserIdAndPointsEarnedGreaterThan("u1", 0)).thenReturn(80L);

        UserPrediction single = createPrediction("p1", "w1", 10);
        when(userPredictionRepository.findByUserId(eq("u1"), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(single)));

        UserStatsResponse result = userService.getUserStats("u1");

        assertEquals(100, result.totalPredictions());
        assertEquals(80, result.correctPredictions());
        assertEquals(1, result.recentPredictions().size());
    }
}
