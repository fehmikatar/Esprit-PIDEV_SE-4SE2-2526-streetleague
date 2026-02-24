package tn.esprit._4se2.pi.Services;

import tn.esprit._4se2.pi.Entities.TeamLineup;

public interface TeamLineupServiceInterface {
    TeamLineup getByMatchAndTeam(Long matchId, Long teamId);
    TeamLineup create(TeamLineup teamLineup);
    void delete(Long id);
}
