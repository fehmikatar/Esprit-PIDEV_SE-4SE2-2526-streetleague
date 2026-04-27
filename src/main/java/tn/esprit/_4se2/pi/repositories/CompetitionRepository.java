package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit._4se2.pi.entities.Competition;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {
    
    java.util.List<tn.esprit._4se2.pi.entities.Competition> findByStatus(tn.esprit._4se2.pi.Enum.CompetitionStatus status);

    @org.springframework.data.jpa.repository.Query("SELECT c FROM Competition c " +
           "WHERE (:keyword IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "  OR LOWER(c.location) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "  OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    java.util.List<tn.esprit._4se2.pi.entities.Competition> searchCompetitions(@org.springframework.data.repository.query.Param("keyword") String keyword);
}
