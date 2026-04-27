package tn.esprit._4se2.pi.services.TeamJoinRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.Enum.JoinRequestStatus;
import tn.esprit._4se2.pi.Enum.Role;
import tn.esprit._4se2.pi.entities.Team;
import tn.esprit._4se2.pi.entities.TeamJoinRequest;
import tn.esprit._4se2.pi.entities.TeamMember;
import tn.esprit._4se2.pi.entities.TeamMemberId;
import tn.esprit._4se2.pi.entities.Community;
import tn.esprit._4se2.pi.entities.CommunityMember;
import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.repositories.CommunityMemberRepository;
import tn.esprit._4se2.pi.repositories.CommunityRepository;
import tn.esprit._4se2.pi.repositories.TeamJoinRequestRepository;
import tn.esprit._4se2.pi.repositories.TeamMemberRepository;
import tn.esprit._4se2.pi.repositories.TeamRepository;
import tn.esprit._4se2.pi.repositories.UserRepository;
import tn.esprit._4se2.pi.services.chat.ChatService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamJoinRequestService {

    private final TeamJoinRequestRepository teamJoinRequestRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository communityMemberRepository;
    private final UserRepository userRepository;
    private final ChatService chatService;

    public Map<String, Object> createJoinRequest(Long teamId, String message) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found: " + teamId));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = resolveCurrentUser(authentication);
        if (currentUser == null) {
            throw new AccessDeniedException("Authenticated user is required");
        }

        if (!hasPlayerAuthority(authentication)) {
            throw new AccessDeniedException("Only players can request to join a team");
        }

        if (teamMemberRepository.existsByTeam_IdAndUser_Id(teamId, currentUser.getId())) {
            throw new IllegalStateException("You are already a member of this team");
        }

        if (teamJoinRequestRepository.existsByTeam_IdAndUser_IdAndStatus(teamId, currentUser.getId(),
                JoinRequestStatus.PENDING)) {
            throw new IllegalStateException("A join request is already pending for this team");
        }

        TeamJoinRequest joinRequest = new TeamJoinRequest();
        joinRequest.setTeam(team);
        joinRequest.setUser(currentUser);
        joinRequest.setMessage(message);
        joinRequest.setStatus(JoinRequestStatus.PENDING);
        joinRequest.setCreatedAt(LocalDateTime.now());

        TeamJoinRequest saved = teamJoinRequestRepository.save(joinRequest);

        Map<String, Object> response = new HashMap<>();
        response.put("requestId", saved.getId());
        response.put("teamId", teamId);
        response.put("status", saved.getStatus().name());
        response.put("message", "Join request sent to admin");
        return response;
    }

    public Map<String, Object> approveJoinRequest(Long requestId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!hasAdminAuthority(authentication)) {
            throw new AccessDeniedException("Only admins can approve join requests");
        }

        TeamJoinRequest request = teamJoinRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Join request not found: " + requestId));

        if (request.getStatus() != JoinRequestStatus.PENDING) {
            throw new IllegalStateException("Join request is not pending");
        }

        if (teamMemberRepository.existsByTeam_IdAndUser_Id(request.getTeam().getId(), request.getUser().getId())) {
            throw new IllegalStateException("User is already a member of this team");
        }

        TeamMember member = new TeamMember();
        member.setId(new TeamMemberId(request.getUser().getId(), request.getTeam().getId()));
        member.setUser(request.getUser());
        member.setTeam(request.getTeam());
        member.setRole(TeamMember.Role.MEMBER);
        teamMemberRepository.save(member);

        addUserToCommunityIfAvailable(request.getTeam(), request.getUser());

        // Ensure the team chatroom is ready as soon as membership is approved.
        chatService.getOrCreateTeamRoom(request.getTeam().getId());

        request.setStatus(JoinRequestStatus.APPROVED);
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewedBy(resolveCurrentUser(authentication));
        teamJoinRequestRepository.save(request);

        Map<String, Object> response = new HashMap<>();
        response.put("requestId", request.getId());
        response.put("teamId", request.getTeam().getId());
        response.put("userId", request.getUser().getId());
        response.put("status", request.getStatus().name());
        response.put("message", "Join request approved");
        return response;
    }

    public Map<String, Object> rejectJoinRequest(Long requestId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!hasAdminAuthority(authentication)) {
            throw new AccessDeniedException("Only admins can reject join requests");
        }

        TeamJoinRequest request = teamJoinRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Join request not found: " + requestId));

        if (request.getStatus() != JoinRequestStatus.PENDING) {
            throw new IllegalStateException("Join request is not pending");
        }

        request.setStatus(JoinRequestStatus.REJECTED);
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewedBy(resolveCurrentUser(authentication));
        teamJoinRequestRepository.save(request);

        Map<String, Object> response = new HashMap<>();
        response.put("requestId", request.getId());
        response.put("teamId", request.getTeam().getId());
        response.put("userId", request.getUser().getId());
        response.put("status", request.getStatus().name());
        response.put("message", "Join request rejected");
        return response;
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getPendingRequestsForAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!hasAdminAuthority(authentication)) {
            throw new AccessDeniedException("Only admins can read join requests");
        }

        return teamJoinRequestRepository.findAllByStatusWithRelations(JoinRequestStatus.PENDING)
                .stream()
                .map(this::toAdminListItem)
                .collect(Collectors.toList());
    }

    private Map<String, Object> toAdminListItem(TeamJoinRequest request) {
        Map<String, Object> item = new HashMap<>();
        item.put("id", request.getId());
        item.put("teamId", request.getTeam().getId());
        item.put("teamName", request.getTeam().getName());
        item.put("userId", request.getUser().getId());
        item.put("userEmail", request.getUser().getEmail());
        item.put("firstName", request.getUser().getFirstName());
        item.put("lastName", request.getUser().getLastName());
        item.put("message", request.getMessage());
        item.put("status", request.getStatus().name());
        item.put("createdAt", request.getCreatedAt());
        return item;
    }

    private boolean hasPlayerAuthority(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities() == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_PLAYER"::equals);
    }

    private boolean hasAdminAuthority(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities() == null) {
            return hasAdminRoleInDb(authentication);
        }
        boolean hasAuthority = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
        return hasAuthority || hasAdminRoleInDb(authentication);
    }

    private boolean hasAdminRoleInDb(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            return false;
        }
        return userRepository.findByEmail(authentication.getName())
                .map(user -> user.getRole() == Role.ROLE_ADMIN)
                .orElse(false);
    }

    private void addUserToCommunityIfAvailable(Team team, User user) {
        if (team == null || team.getCategory() == null || user == null) {
            return;
        }

        Community community = communityRepository.findByCategory_Id(team.getCategory().getId())
                .orElse(null);
        if (community == null) {
            return;
        }

        if (communityMemberRepository.existsByCommunity_IdAndUser_Id(community.getId(), user.getId())) {
            return;
        }

        CommunityMember communityMember = CommunityMember.builder()
                .community(community)
                .user(user)
                .joinedAt(LocalDateTime.now())
                .build();
        communityMemberRepository.save(communityMember);
    }

    private User resolveCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            return null;
        }
        return userRepository.findByEmail(authentication.getName()).orElse(null);
    }

    // Dans TeamJoinRequestService.java
    public void syncUserCommunities(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();

        teamMemberRepository.findByUser_Id(userId).forEach(member -> {
            addUserToCommunityIfAvailable(member.getTeam(), user);
        });
    }
}