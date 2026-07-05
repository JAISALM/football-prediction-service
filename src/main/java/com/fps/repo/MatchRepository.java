package com.fps.repo;

import com.fps.enums.MatchStatus;
import com.fps.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, String> {
    List<Match> findByStatus(MatchStatus status);
}
