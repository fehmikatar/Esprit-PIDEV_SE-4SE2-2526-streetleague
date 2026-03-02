package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit._4se2.pi.entities.TeamLineup;

public interface TeamLineupRepository extends JpaRepository<TeamLineup, Long> {
    TeamLineup findByMatchIdAndTeamId(Long matchId, Long teamId);
}
