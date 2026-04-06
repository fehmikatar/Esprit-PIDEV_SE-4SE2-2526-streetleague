package tn.esprit._4se2.pi.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit._4se2.pi.Entities.TeamLineup;

public interface TeamLineupRepository extends JpaRepository<TeamLineup, Long> {
    TeamLineup findByMatchIdAndTeamId(Long matchId, Long teamId);
}
