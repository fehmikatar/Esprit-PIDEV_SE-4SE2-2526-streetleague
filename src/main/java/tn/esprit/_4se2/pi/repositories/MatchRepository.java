package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit._4se2.pi.entities.Match;
import tn.esprit._4se2.pi.Enum.MatchStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByCompetitionId(Long competitionId);
    List<Match> findByHomeTeamIdOrAwayTeamId(Long homeTeamId, Long awayTeamId);
    List<Match> findByCompetitionIdAndStatus(Long competitionId, MatchStatus status);

    @Query("SELECT m FROM Match m WHERE m.competitionId = :compId AND (m.homeTeamId = :teamId OR m.awayTeamId = :teamId)")
    List<Match> findByCompetitionIdAndTeamId(@Param("compId") Long competitionId, @Param("teamId") Long teamId);

    @Query("SELECT m FROM Match m WHERE m.competitionId = :compId AND m.scheduledAt BETWEEN :dayStart AND :dayEnd")
    List<Match> findByCompetitionIdAndScheduledAtBetween(
            @Param("compId") Long competitionId,
            @Param("dayStart") LocalDateTime dayStart,
            @Param("dayEnd") LocalDateTime dayEnd);

    @Query("SELECT m FROM Match m WHERE (m.homeTeamId = :teamId OR m.awayTeamId = :teamId) AND m.scheduledAt BETWEEN :dayStart AND :dayEnd")
    List<Match> findByTeamIdAndScheduledAtBetween(
            @Param("teamId") Long teamId,
            @Param("dayStart") LocalDateTime dayStart,
            @Param("dayEnd") LocalDateTime dayEnd);
}
