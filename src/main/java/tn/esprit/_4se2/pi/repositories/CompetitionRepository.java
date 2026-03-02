package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit._4se2.pi.entities.Competition;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {
}
