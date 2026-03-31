package tn.esprit._4se2.pi.services.Team;

import tn.esprit._4se2.pi.entities.Team;

import java.util.List;

public interface TeamServiceInterface {

    Team create(Team team);
    List<Team> getAll();
    Team getById(Long id);
    Team getByName(String name);
    Team update(Long id, Team updatedTeam);
    void delete(Long id);
}
