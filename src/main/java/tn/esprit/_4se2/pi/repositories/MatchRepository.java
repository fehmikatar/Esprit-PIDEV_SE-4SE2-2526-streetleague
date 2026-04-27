package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit._4se2.pi.entities.Match;

import java.util.List;

public interface MatchRepository extends JpaRepository<Match, Long> {
    List<Match> findByCompetitionId(Long competitionId);
    List<Match> findByHomeTeamIdOrAwayTeamId(Long homeTeamId, Long awayTeamId);

    List<Match> findByStatusAndScheduledAtBefore(tn.esprit._4se2.pi.Enum.MatchStatus status, java.time.LocalDateTime dateTime);


    @org.springframework.data.jpa.repository.Query("SELECT new tn.esprit._4se2.pi.dto.Match.MatchDTOs$MatchResponseDTO(" +
           "m.id, m.competitionId, c.name, m.homeTeamId, ht.name, m.awayTeamId, at.name, " +
           "m.scheduledAt, m.venue, m.status, m.homeScore, m.awayScore) " +
           "FROM Match m " +
           "JOIN Competition c ON m.competitionId = c.id " +
           "JOIN Team ht ON m.homeTeamId = ht.id " +
           "JOIN Team at ON m.awayTeamId = at.id " +
           "WHERE (:keyword IS NULL OR LOWER(m.venue) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "  OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "  OR LOWER(ht.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "  OR LOWER(at.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    java.util.List<tn.esprit._4se2.pi.dto.Match.MatchDTOs.MatchResponseDTO> searchMatchesDetailed(@org.springframework.data.repository.query.Param("keyword") String keyword);

    @org.springframework.data.jpa.repository.Query("SELECT m FROM Match m " +
           "WHERE (m.homeScore + m.awayScore) > 3 " +
           "ORDER BY (m.homeScore + m.awayScore) DESC")
    java.util.List<Match> findHighScoringMatches();
}
