package tn.esprit._4se2.pi.services.TeamMember;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.entities.Team;
import tn.esprit._4se2.pi.entities.TeamMember;
import tn.esprit._4se2.pi.entities.TeamMemberId;
import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.repositories.TeamMemberRepository;
import tn.esprit._4se2.pi.repositories.TeamRepository;
import tn.esprit._4se2.pi.repositories.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamMemberService implements ITeamMemberService {

    private final TeamMemberRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public TeamMember addTeamMember(TeamMember teamMember) {
        // Initialiser l'ID composite si nécessaire
        if (teamMember.getId() == null) {
            teamMember.setId(new TeamMemberId());
        }

        // Récupérer et associer l'utilisateur
        if (teamMember.getUser() != null && teamMember.getUser().getId() != null) {
            User user = userRepository.findById(teamMember.getUser().getId())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id : " + teamMember.getUser().getId()));
            teamMember.setUser(user);
            teamMember.getId().setUserId(user.getId());
        } else {
            throw new RuntimeException("L'utilisateur est requis pour ajouter un membre");
        }

        // Récupérer et associer l'équipe
        if (teamMember.getTeam() != null && teamMember.getTeam().getId() != null) {
            Team team = teamRepository.findById(teamMember.getTeam().getId())
                    .orElseThrow(() -> new RuntimeException("Équipe non trouvée avec l'id : " + teamMember.getTeam().getId()));
            teamMember.setTeam(team);
            teamMember.getId().setTeamId(team.getId());
        } else {
            throw new RuntimeException("L'équipe est requise pour ajouter un membre");
        }

        // Vérifier l'unicité (la contrainte de la base fera de même)
        if (teamMemberRepository.existsById(teamMember.getId())) {
            throw new RuntimeException("Ce membre est déjà présent dans l'équipe");
        }

        return teamMemberRepository.save(teamMember);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamMember> getAllTeamMembers() {
        return teamMemberRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public TeamMember getTeamMemberById(TeamMemberId id) {
        return teamMemberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Membre d'équipe non trouvé avec userId : " + id.getUserId() + " et teamId : " + id.getTeamId()));
    }

    @Override
    @Transactional
    public TeamMember updateTeamMember(TeamMember teamMember) {
        // Récupérer l'entité existante
        TeamMember existingMember = getTeamMemberById(teamMember.getId());

        // Mettre à jour le rôle uniquement (les associations user/team sont fixes)
        if (teamMember.getRole() != null) {
            existingMember.setRole(teamMember.getRole());
        }

        return existingMember; // Les modifications sont persistées automatiquement
    }

    @Override
    @Transactional
    public void deleteTeamMember(TeamMemberId id) {
        teamMemberRepository.deleteById(id);
    }
}