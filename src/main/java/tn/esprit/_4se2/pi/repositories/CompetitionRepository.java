package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit._4se2.pi.Enum.CompetitionStatus;
import tn.esprit._4se2.pi.entities.Competition;

import java.util.List;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {
    List<Competition> findByStatus(CompetitionStatus status);
}
