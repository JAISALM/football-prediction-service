package com.fps.repo;

import com.fps.model.UserPrediction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserPredictionRepository extends JpaRepository<UserPrediction, String> {
    Optional<UserPrediction> findByUserIdAndWindowId(String userId, String windowId);
    List<UserPrediction> findByWindowId(String windowId);

    @EntityGraph(attributePaths = {"user", "window"})
    Page<UserPrediction> findByWindowId(String windowId, Pageable pageable);
}
