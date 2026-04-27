package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit._4se2.pi.entities.MatchEvent;

import java.util.List;

public interface MatchEventRepository extends JpaRepository<MatchEvent, Long> {
    List<MatchEvent> findByMatchId(Long matchId);


    @org.springframework.data.jpa.repository.Query("SELECT me FROM MatchEvent me " +
           "LEFT JOIN Player p ON me.playerId = p.id " +
           "LEFT JOIN Team t ON me.teamId = t.id " +
           "WHERE (:keyword IS NULL OR LOWER(me.description) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "  OR LOWER(p.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "  OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "  OR LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<MatchEvent> searchEvents(@org.springframework.data.repository.query.Param("keyword") String keyword);
}
