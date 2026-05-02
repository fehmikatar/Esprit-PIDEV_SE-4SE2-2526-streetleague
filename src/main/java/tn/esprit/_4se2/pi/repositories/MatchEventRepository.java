package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit._4se2.pi.entities.MatchEvent;
import tn.esprit._4se2.pi.Enum.MatchEventType;

import java.util.List;

public interface MatchEventRepository extends JpaRepository<MatchEvent, Long> {
    List<MatchEvent> findByMatchId(Long matchId);
    List<MatchEvent> findByMatchIdOrderByMinuteAsc(Long matchId);
    List<MatchEvent> findByMatchIdAndEventType(Long matchId, MatchEventType eventType);
    List<MatchEvent> findByMatchIdAndPlayerId(Long matchId, Long playerId);
    List<MatchEvent> findByMatchIdAndTeamId(Long matchId, Long teamId);

    // For competition-wide analytics
    List<MatchEvent> findByMatchIdIn(List<Long> matchIds);
    List<MatchEvent> findByMatchIdInAndEventType(List<Long> matchIds, MatchEventType eventType);
}
