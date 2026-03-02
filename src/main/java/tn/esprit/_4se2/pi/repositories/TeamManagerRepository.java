package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.entities.TeamManager;
import java.util.Optional;

@Repository
public interface TeamManagerRepository extends JpaRepository<TeamManager, Long> {
    Optional<TeamManager> findByTeamCode(String teamCode);
    Optional<TeamManager> findByEmail(String email);
}