package tn.esprit._4se2.pi.services.TeamMember;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.Enum.MemberStatus;
import tn.esprit._4se2.pi.entities.Team;
import tn.esprit._4se2.pi.entities.TeamMember;
import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.repositories.TeamMemberRepository;
import tn.esprit._4se2.pi.repositories.TeamRepository;
import tn.esprit._4se2.pi.repositories.UserRepository;

import java.time.LocalDateTime;
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
        // Récupérer et associer l'utilisateur
        if (teamMember.getUser() != null && teamMember.getUser().getId() != null) {
            User user = userRepository.findById(teamMember.getUser().getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Utilisateur non trouvé avec l'id : " + teamMember.getUser().getId()));
            teamMember.setUser(user);
        } else {
            throw new RuntimeException("L'utilisateur est requis pour ajouter un membre");
        }

        // Récupérer et associer l'équipe
        if (teamMember.getTeam() != null && teamMember.getTeam().getId() != null) {
            Team team = teamRepository.findById(teamMember.getTeam().getId())
                    .orElseThrow(() -> new RuntimeException(
                            "Équipe non trouvée avec l'id : " + teamMember.getTeam().getId()));
            teamMember.setTeam(team);
        } else {
            throw new RuntimeException("L'équipe est requise pour ajouter un membre");
        }

        // Vérifier l'unicité (un utilisateur ne peut être actif qu'une fois dans une
        // équipe)
        if (teamMemberRepository.existsByTeamIdAndUserIdAndStatus(
                teamMember.getTeam().getId(), teamMember.getUser().getId(), MemberStatus.ACTIVE)) {
            throw new RuntimeException("Ce membre est déjà actif dans l'équipe");
        }

        // Définir la date d'ajout
        if (teamMember.getJoinedAt() == null) {
            teamMember.setJoinedAt(LocalDateTime.now());
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
    public TeamMember getTeamMemberById(Long id) {
        return teamMemberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "Membre d'équipe non trouvé avec l'id : " + id));
    }

    @Override
    @Transactional
    public TeamMember updateTeamMember(TeamMember teamMember) {
        // Récupérer l'entité existante
        TeamMember existingMember = getTeamMemberById(teamMember.getId());

        // Mettre à jour le rôle si fourni
        if (teamMember.getTeamRole() != null) {
            existingMember.setTeamRole(teamMember.getTeamRole());
        }

        // Mettre à jour le statut si fourni
        if (teamMember.getStatus() != null) {
            existingMember.setStatus(teamMember.getStatus());
        }

        return teamMemberRepository.save(existingMember);
    }

    @Override
    @Transactional
    public void deleteTeamMember(Long id) {
        teamMemberRepository.deleteById(id);
    }
}