package com.fps.service;

import com.fps.dto.PredictionResponse;
import com.fps.dto.UserStatsResponse;
import com.fps.entities.User;
import com.fps.entities.UserPrediction;
import com.fps.exception.ResourceNotFoundException;
import com.fps.repo.UserPredictionRepository;
import com.fps.repo.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserPredictionRepository userPredictionRepository;

    public UserService(UserRepository userRepository, UserPredictionRepository userPredictionRepository) {
        this.userRepository = userRepository;
        this.userPredictionRepository = userPredictionRepository;
    }

    public UserStatsResponse getUserStats(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        List<UserPrediction> recentPredictions = userPredictionRepository.findByUserId(
                userId,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt"))
        );

        long totalPredictions = recentPredictions.size();
        long correctPredictions = recentPredictions.stream()
                .filter(p -> p.getPointsEarned() != null && p.getPointsEarned() > 0)
                .count();

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

    public User createUser(String username) {
        User user = User.builder()
                .username(username)
                .totalPoints(100)
                .build();
        return userRepository.save(user);
    }
}
