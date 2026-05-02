package tn.esprit._4se2.pi.services.Team;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.Enum.JoinRequestStatus;
import tn.esprit._4se2.pi.entities.Team;
import tn.esprit._4se2.pi.entities.TeamJoinRequest;
import tn.esprit._4se2.pi.entities.TeamMember;
import tn.esprit._4se2.pi.entities.TeamMemberId;
import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.repositories.TeamJoinRequestRepository;
import tn.esprit._4se2.pi.repositories.TeamMemberRepository;
import tn.esprit._4se2.pi.repositories.TeamRepository;
import tn.esprit._4se2.pi.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamJoinRequestService implements TeamJoinRequestServiceInterface {

    private final TeamJoinRequestRepository joinRequestRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository; // ← AJOUTÉ

    @Override
    public TeamJoinRequest createJoinRequest(Long teamId, String userEmail, String message) {
        Team team = teamRepository.findById(teamId).orElse(null);
        if (team == null) {
            throw new RuntimeException("Team not found");
        }

        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        boolean hasPendingRequest = joinRequestRepository.existsByTeamAndUserAndStatus(
                team, user, JoinRequestStatus.PENDING);

        if (hasPendingRequest) {
            throw new RuntimeException("You already have a pending request for this team");
        }

        TeamJoinRequest joinRequest = TeamJoinRequest.builder()
                .team(team)
                .user(user)
                .message(message)
                .status(JoinRequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        return joinRequestRepository.save(joinRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamJoinRequest> getUserJoinRequests(String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not found");
        }
        return joinRequestRepository.findByUser(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamJoinRequest> getTeamJoinRequests(Long teamId) {
        Team team = teamRepository.findById(teamId).orElse(null);
        if (team == null) {
            throw new RuntimeException("Team not found");
        }
        return joinRequestRepository.findByTeamAndStatus(team, JoinRequestStatus.PENDING);
    }



    @Override
    public TeamJoinRequest rejectJoinRequest(Long requestId, String reviewerEmail) {
        TeamJoinRequest request = joinRequestRepository.findById(requestId).orElse(null);
        if (request == null) {
            throw new RuntimeException("Join request not found");
        }

        if (request.getStatus() != JoinRequestStatus.PENDING) {
            throw new RuntimeException("This request has already been reviewed");
        }

        User reviewer = userRepository.findByEmail(reviewerEmail).orElse(null);
        if (reviewer == null) {
            throw new RuntimeException("Reviewer not found");
        }

        request.setStatus(JoinRequestStatus.REJECTED);
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewedBy(reviewer);

        return joinRequestRepository.save(request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamJoinRequest> getAllPendingJoinRequests() {
        System.out.println("🔍 Récupération de TOUTES les demandes PENDING pour l'admin");

        // Récupérer toutes les demandes avec le statut PENDING
        List<TeamJoinRequest> requests = joinRequestRepository.findByStatus(JoinRequestStatus.PENDING);

        System.out.println("📊 Nombre de demandes trouvées: " + requests.size());

        return requests;
    }

    @Override
    public TeamJoinRequest approveJoinRequest(Long requestId, String reviewerEmail) {
        TeamJoinRequest request = joinRequestRepository.findById(requestId).orElse(null);
        if (request == null) {
            throw new RuntimeException("Join request not found");
        }

        if (request.getStatus() != JoinRequestStatus.PENDING) {
            throw new RuntimeException("This request has already been reviewed");
        }

        User reviewer = userRepository.findByEmail(reviewerEmail).orElse(null);
        if (reviewer == null) {
            throw new RuntimeException("Reviewer not found");
        }

        // ✅ Changer le statut
        request.setStatus(JoinRequestStatus.APPROVED);
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewedBy(reviewer);

        // ✅✅ NOUVEAU : Ajouter l'utilisateur à l'équipe
        Long teamId = request.getTeam().getId();
        Long userId = request.getUser().getId();

        // Vérifier qu'il n'est pas déjà membre (au cas où)
        if (!teamMemberRepository.existsByTeamIdAndUserId(teamId, userId)) {
            TeamMemberId memberId = new TeamMemberId(userId, teamId);

            TeamMember newMember = new TeamMember();
            newMember.setId(memberId);
            newMember.setTeam(request.getTeam());
            newMember.setUser(request.getUser());
            newMember.setRole(TeamMember.Role.MEMBER);

            teamMemberRepository.save(newMember);

            System.out.println("✅ Utilisateur " + userId + " ajouté à l'équipe " + teamId);
        } else {
            System.out.println("⚠️ Utilisateur déjà membre de l'équipe");
        }

        return joinRequestRepository.save(request);
    }

}