package tn.esprit._4se2.pi.services.Team;

import org.springframework.stereotype.Service;
import tn.esprit._4se2.pi.entities.Team;
import tn.esprit._4se2.pi.repositories.TeamRepository;
import tn.esprit._4se2.pi.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    public TeamService(TeamRepository teamRepository, UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
    }

    // Créer une équipe
    public Team create(Team team, Long userId) {
        if (team.getCreatedAt() == null) {
            team.setCreatedAt(LocalDateTime.now());
        }

        if (team.getCreatedBy() == null) {
            // Priority 1: Use userId from request parameter
            if (userId != null) {
                userRepository.findById(userId).ifPresent(team::setCreatedBy);
            }
            
            // Priority 2: Use Security Context
            if (team.getCreatedBy() == null) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.isAuthenticated() && !authentication.getPrincipal().equals("anonymousUser")) {
                    String email = authentication.getName();
                    userRepository.findByEmail(email).ifPresent(team::setCreatedBy);
                }
            }
            
            // Priority 3: Fallback to the first user in the DB (for development/testing)
            if (team.getCreatedBy() == null) {
                userRepository.findAll().stream().findFirst().ifPresent(team::setCreatedBy);
            }
            
            if (team.getCreatedBy() == null) {
                throw new RuntimeException("Could not assign a creator to the team (created_by_id cannot be null). User ID provided was: " + userId + ". Please ensure this user exists in the database.");
            }
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