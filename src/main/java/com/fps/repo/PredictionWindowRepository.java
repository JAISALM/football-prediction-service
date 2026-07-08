package com.fps.repo;

import com.fps.entities.PredictionWindow;
import com.fps.enums.WindowStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PredictionWindowRepository extends JpaRepository<PredictionWindow, String> {
    @EntityGraph(attributePaths = {"match"})
    Page<PredictionWindow> findByMatchIdAndStatus(String matchId, WindowStatus status, Pageable pageable);
}
