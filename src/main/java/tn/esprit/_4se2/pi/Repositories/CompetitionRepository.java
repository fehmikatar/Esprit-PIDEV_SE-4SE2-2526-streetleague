package tn.esprit._4se2.pi.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit._4se2.pi.Entities.Competition;

public interface CompetitionRepository extends JpaRepository<Competition, Long> {
}
