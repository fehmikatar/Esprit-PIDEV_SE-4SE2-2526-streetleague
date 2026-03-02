package tn.esprit._4se2.pi.services;

import org.springframework.stereotype.Service;
import tn.esprit._4se2.pi.entities.Team;
import tn.esprit._4se2.pi.repositories.TeamRepository;

import java.util.List;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    // Créer une équipe
    public Team create(Team team) {
        return teamRepository.save(team);
    }

    // Récupérer toutes les équipes
    public List<Team> getAll() {
        return teamRepository.findAll();
    }

    // Récupérer une équipe par son ID
    public Team getById(Long id) {
        return teamRepository.findById(id).orElseThrow(() -> new RuntimeException("Team not found"));
    }

    // Récupérer une équipe par son nom
    public Team getByName(String name) {
        return teamRepository.findByName(name);
    }

    // Mettre à jour une équipe
    public Team update(Long id, Team updatedTeam) {
        Team existingTeam = getById(id);
        existingTeam.setName(updatedTeam.getName());
        existingTeam.setDescription(updatedTeam.getDescription());
        existingTeam.setLogoUrl(updatedTeam.getLogoUrl());
        return teamRepository.save(existingTeam);
    }

    // Supprimer une équipe
    public void delete(Long id) {
        teamRepository.deleteById(id);
    }
}
