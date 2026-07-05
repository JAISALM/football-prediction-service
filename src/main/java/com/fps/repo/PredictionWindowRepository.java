package com.fps.repo;

import com.fps.enums.WindowStatus;
import com.fps.model.PredictionWindow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PredictionWindowRepository extends JpaRepository<PredictionWindow, String> {
    List<PredictionWindow> findByMatchIdAndStatus(String matchId, WindowStatus status);

    @EntityGraph(attributePaths = {"match"})
    Page<PredictionWindow> findByMatchIdAndStatus(String matchId, WindowStatus status, Pageable pageable);
}
