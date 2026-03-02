package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit._4se2.pi.entities.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Team findByName(String name);
}
