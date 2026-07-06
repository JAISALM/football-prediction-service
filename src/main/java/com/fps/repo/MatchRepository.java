package com.fps.repo;

import com.fps.enums.MatchStatus;
import com.fps.entities.Match;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MatchRepository extends JpaRepository<Match, String> {
    List<Match> findByStatus(MatchStatus status);

    Page<Match> findByStatus(MatchStatus status, Pageable pageable);
}
