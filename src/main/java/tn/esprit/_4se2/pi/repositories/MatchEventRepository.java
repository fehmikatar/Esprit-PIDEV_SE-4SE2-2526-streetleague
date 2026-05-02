package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit._4se2.pi.entities.MatchEvent;

import java.util.List;

public interface MatchEventRepository extends JpaRepository<MatchEvent, Long> {
    List<MatchEvent> findByMatchId(Long matchId);
    List<MatchEvent> findByMatchIdOrderByMinuteAsc(Long matchId);
    List<MatchEvent> findByMatchIdAndPlayerId(Long matchId, Long playerId);
    List<MatchEvent> findByMatchIdIn(List<Long> matchIds);
}
