package com.fps.repo;

import com.fps.entities.Match;
import com.fps.enums.MatchStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MatchRepository extends JpaRepository<Match, String> {
    Page<Match> findByStatus(MatchStatus status, Pageable pageable);
}
