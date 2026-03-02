package tn.esprit._4se2.pi.services.Team;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.entities.Team;
import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.repositories.TeamRepository;
import tn.esprit._4se2.pi.repositories.UserRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService implements ITeamService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Team addTeam(Team team) {
        // Initialiser la date de création
        team.setCreatedAt(LocalDate.now());

        // Gérer le responsable (obligatoire selon la logique métier)
        if (team.getResponsible() != null && team.getResponsible().getId() != null) {
            User responsible = userRepository.findById(team.getResponsible().getId())
                    .orElseThrow(() -> new RuntimeException("Responsable non trouvé avec l'id : " + team.getResponsible().getId()));
            team.setResponsible(responsible);
        } else {
            throw new RuntimeException("L'équipe doit avoir un responsable");
        }

        // Les membres et messages ne sont pas gérés ici (ils seront ajoutés via d'autres endpoints)
        return teamRepository.save(team);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Team getTeamById(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Équipe non trouvée avec l'id : " + id));
    }

    @Override
    @Transactional
    public Team updateTeam(Team team) {
        // Récupérer l'équipe existante
        Team existingTeam = getTeamById(team.getId());

        // Mettre à jour le nom si fourni
        if (team.getName() != null) {
            existingTeam.setName(team.getName());
        }

        // Mettre à jour le responsable si un nouvel ID est fourni
        if (team.getResponsible() != null && team.getResponsible().getId() != null) {
            User responsible = userRepository.findById(team.getResponsible().getId())
                    .orElseThrow(() -> new RuntimeException("Responsable non trouvé avec l'id : " + team.getResponsible().getId()));
            existingTeam.setResponsible(responsible);
        }
        // Si team.getResponsible() est null, on garde l'ancien responsable (pas de changement)

        // Les membres et messages ne sont pas modifiés ici
        return existingTeam; // Les modifications sont automatiquement persistées grâce à @Transactional
    }

    @Override
    @Transactional
    public void deleteTeam(Long id) {
        teamRepository.deleteById(id);
    }
}