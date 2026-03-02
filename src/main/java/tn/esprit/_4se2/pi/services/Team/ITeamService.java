package tn.esprit._4se2.pi.services.Team;

import tn.esprit._4se2.pi.entities.Team;

import java.util.List;

public interface ITeamService {
    Team addTeam(Team team);

    List<Team> getAllTeams();

    Team getTeamById(Long id);

    Team updateTeam(Team team);

    void deleteTeam(Long id);
}
