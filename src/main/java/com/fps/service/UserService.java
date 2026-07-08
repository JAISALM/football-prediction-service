package com.fps.service;

import com.fps.dto.PredictionResponse;
import com.fps.dto.UserCreateResponse;
import com.fps.dto.UserStatsResponse;
import com.fps.entities.User;
import com.fps.entities.UserPrediction;
import com.fps.exception.ResourceNotFoundException;
import com.fps.repo.UserPredictionRepository;
import com.fps.repo.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
public class UserService {

    static final int DEFAULT_USER_POINTS = 100;
    static final int RECENT_PREDICTIONS_LIMIT = 10;

    private final UserRepository userRepository;
    private final UserPredictionRepository userPredictionRepository;

    public UserService(UserRepository userRepository, UserPredictionRepository userPredictionRepository) {
        this.userRepository = userRepository;
        this.userPredictionRepository = userPredictionRepository;
    }

    @Transactional(readOnly = true)
    public UserStatsResponse getUserStats(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        long totalPredictions = userPredictionRepository.countByUserId(userId);
        long correctPredictions = userPredictionRepository.countByUserIdAndPointsEarnedGreaterThan(userId, 0);

        Page<UserPrediction> recentPredictions = userPredictionRepository.findByUserId(
                userId,
                PageRequest.of(0, RECENT_PREDICTIONS_LIMIT, Sort.by(Sort.Direction.DESC, "createdAt"))
        );

        List<PredictionResponse> recentResponses = recentPredictions.stream()
                .map(p -> new PredictionResponse(
                        p.getId(),
                        p.getWindow().getId(),
                        p.getPredictedValue(),
                        p.getPointsEarned()
                ))
                .toList();

        return new UserStatsResponse(
                user.getId(),
                user.getUsername(),
                user.getTotalPoints(),
                (int) totalPredictions,
                (int) correctPredictions,
                recentResponses
        );
    }

    @Transactional
    public UserCreateResponse createUser(String username) {
        User user = User.builder()
                .username(username)
                .totalPoints(DEFAULT_USER_POINTS)
                .build();
        user = userRepository.save(user);
        log.info("Created user {} with username {}", user.getId(), username);
        return toCreateResponse(user);
    }

    private UserCreateResponse toCreateResponse(User user) {
        return new UserCreateResponse(
                user.getId(),
                user.getUsername(),
                user.getTotalPoints()
        );
    }
}
