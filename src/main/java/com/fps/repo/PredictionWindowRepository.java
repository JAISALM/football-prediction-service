package com.fps.repo;

import com.fps.enums.WindowStatus;
import com.fps.model.PredictionWindow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PredictionWindowRepository extends JpaRepository<PredictionWindow, String> {
    List<PredictionWindow> findByMatchIdAndStatus(String matchId, WindowStatus status);
}
