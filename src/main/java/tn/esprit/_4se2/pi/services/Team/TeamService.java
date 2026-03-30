package tn.esprit._4se2.pi.services.Team;

import org.springframework.stereotype.Service;
import tn.esprit._4se2.pi.entities.Team;
import tn.esprit._4se2.pi.repositories.TeamRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    // Créer une équipe
    public Team create(Team team) {
        if (team.getCreatedAt() == null) {
            team.setCreatedAt(LocalDateTime.now());
        }
        return teamRepository.save(team);
    }

    // Récupérer toutes les équipes
    public List<Team> getAll() {
        return teamRepository.findAll();
    }

    // Récupérer une équipe par son ID
    public Team getById(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found"));
    }

    // Récupérer une équipe par son nom
    public Team getByName(String name) {
        return teamRepository.findByName(name);
    }

    // Mettre à jour une équipe
    public Team update(Long id, Team updatedTeam) {
        Team existingTeam = getById(id);

        existingTeam.setName(updatedTeam.getName());
        existingTeam.setSport(updatedTeam.getSport());
        existingTeam.setLevel(updatedTeam.getLevel());
        existingTeam.setDescription(updatedTeam.getDescription());
        existingTeam.setCity(updatedTeam.getCity());
        existingTeam.setLogo(updatedTeam.getLogo());
        existingTeam.setStatus(updatedTeam.getStatus());

        // à garder seulement si tu veux autoriser la modification du créateur
        existingTeam.setCreatedBy(updatedTeam.getCreatedBy());

        return teamRepository.save(existingTeam);
    }

    // Supprimer une équipe
    public void delete(Long id) {
        teamRepository.deleteById(id);
    }
}