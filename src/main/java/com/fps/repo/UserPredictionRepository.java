package com.fps.repo;

import com.fps.entities.UserPrediction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface UserPredictionRepository extends JpaRepository<UserPrediction, String> {
    @Query("SELECT up FROM UserPrediction up WHERE up.user.id = :userId AND up.window.id = :windowId")
    Optional<UserPrediction> findByUserIdAndWindowId(@Param("userId") String userId, @Param("windowId") String windowId);

    @Query("SELECT up FROM UserPrediction up WHERE up.window.id = :windowId")
    List<UserPrediction> findByWindowId(@Param("windowId") String windowId);

    @EntityGraph(attributePaths = {"user", "window"})
    @Query("SELECT up FROM UserPrediction up WHERE up.window.id = :windowId")
    Page<UserPrediction> findByWindowId(@Param("windowId") String windowId, Pageable pageable);

    @EntityGraph(attributePaths = {"window"})
    @Query("SELECT up FROM UserPrediction up WHERE up.user.id = :userId")
    Page<UserPrediction> findByUserId(@Param("userId") String userId, Pageable pageable);

    @Query("SELECT COUNT(up) FROM UserPrediction up WHERE up.user.id = :userId")
    long countByUserId(@Param("userId") String userId);

    @Query("SELECT COUNT(up) FROM UserPrediction up WHERE up.user.id = :userId AND up.pointsEarned > 0")
    long countByUserIdAndPointsEarnedGreaterThan(@Param("userId") String userId, @Param("points") int points);
}
