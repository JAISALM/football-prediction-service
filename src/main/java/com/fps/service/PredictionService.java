package com.fps.service;

import com.fps.dto.PredictionRequest;
import com.fps.dto.PredictionResponse;
import com.fps.entities.PredictionWindow;
import com.fps.entities.User;
import com.fps.entities.UserPrediction;
import com.fps.enums.WindowStatus;
import com.fps.exception.DuplicatePredictionException;
import com.fps.exception.ResourceNotFoundException;
import com.fps.exception.WindowClosedException;
import com.fps.repo.PredictionWindowRepository;
import com.fps.repo.UserPredictionRepository;
import com.fps.repo.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PredictionService {

    private final UserPredictionRepository userPredictionRepository;
    private final PredictionWindowRepository windowRepository;
    private final UserRepository userRepository;

    public PredictionService(UserPredictionRepository userPredictionRepository,
                             PredictionWindowRepository windowRepository,
                             UserRepository userRepository) {
        this.userPredictionRepository = userPredictionRepository;
        this.windowRepository = windowRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public PredictionResponse submitPrediction(String userId, PredictionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        PredictionWindow window = windowRepository.findById(request.windowId())
                .orElseThrow(() -> new ResourceNotFoundException("Prediction window not found with id: " + request.windowId()));

        if (userPredictionRepository.findByUserIdAndWindowId(userId, request.windowId()).isPresent()) {
            throw new DuplicatePredictionException("User has already predicted for this window");
        }

        if (window.getStatus() != WindowStatus.OPEN) {
            throw new WindowClosedException("Prediction window is no longer open");
        }

        UserPrediction prediction = UserPrediction.builder()
                .user(user)
                .window(window)
                .predictedValue(request.predictedValue())
                .build();

        prediction = userPredictionRepository.save(prediction);
        return toResponse(prediction);
    }

    PredictionResponse toResponse(UserPrediction prediction) {
        return new PredictionResponse(
                prediction.getId(),
                prediction.getWindow().getId(),
                prediction.getPredictedValue(),
                prediction.getPointsEarned()
        );
    }
}
