package tn.esprit._4se2.pi.services;

import tn.esprit._4se2.pi.entities.TeamLineup;

public interface TeamLineupServiceInterface {
    TeamLineup getByMatchAndTeam(Long matchId, Long teamId);
    TeamLineup create(TeamLineup teamLineup);
    void delete(Long id);
}
