package tn.esprit._4se2.pi.Services;

import org.springframework.stereotype.Service;
import tn.esprit._4se2.pi.Entities.TeamLineup;
import tn.esprit._4se2.pi.Repositories.TeamLineupRepository;

@Service
public class TeamLineupService {
    private final TeamLineupRepository teamLineupRepository;

    public TeamLineupService(TeamLineupRepository teamLineupRepository) {
        this.teamLineupRepository = teamLineupRepository;
    }

    public TeamLineup getByMatchAndTeam(Long matchId, Long teamId) {
        return teamLineupRepository.findByMatchIdAndTeamId(matchId, teamId);
    }

    public TeamLineup create(TeamLineup teamLineup) {
        return teamLineupRepository.save(teamLineup);
    }

    public void delete(Long id) {
        teamLineupRepository.deleteById(id);
    }
}
