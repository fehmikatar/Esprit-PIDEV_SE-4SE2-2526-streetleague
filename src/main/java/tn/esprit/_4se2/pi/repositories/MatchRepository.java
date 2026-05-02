package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit._4se2.pi.entities.Match;
import tn.esprit._4se2.pi.Enum.MatchStatus;

import java.time.LocalDateTime;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByCompetitionId(Long competitionId);
    List<Match> findByCompetitionIdAndStatus(Long competitionId, MatchStatus status);
    List<Match> findByStatusAndScheduledAtBefore(MatchStatus status, LocalDateTime scheduledAt);
    List<Match> findByHomeTeamIdOrAwayTeamId(Long homeTeamId, Long awayTeamId);

    default List<Match> findFinishedByTeam(Long teamId) {
        return findByHomeTeamIdOrAwayTeamId(teamId, teamId).stream()
                .filter(match -> MatchStatus.FINISHED.equals(match.getStatus()))
                .toList();
    }
}
