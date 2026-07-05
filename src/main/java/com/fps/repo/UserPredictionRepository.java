package com.fps.repo;

import com.fps.model.UserPrediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserPredictionRepository extends JpaRepository<UserPrediction, String> {
    Optional<UserPrediction> findByUserIdAndWindowId(String userId, String windowId);
    List<UserPrediction> findByWindowId(String windowId);
}
