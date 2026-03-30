package tn.esprit._4se2.pi.services.Team;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.Enum.*;
import tn.esprit._4se2.pi.dto.*;
import tn.esprit._4se2.pi.entities.*;
import tn.esprit._4se2.pi.exception.*;
import tn.esprit._4se2.pi.repositories.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService implements ITeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamJoinRequestRepository joinRequestRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final SportCommunityRepository sportCommunityRepository;
    private final CommunityMemberRepository communityMemberRepository;

    // ──────────────────────────────────────────────
    // CRUD
    // ──────────────────────────────────────────────

    @Override
    @Transactional
    public TeamResponse createTeam(TeamRequest request, Long authenticatedUserId) {
        User creator = findUserOrThrow(authenticatedUserId);
        String validatedSport = resolveSportCategoryOrThrow(request.getSport());

        Team team = Team.builder()
                .name(request.getName())
            .sport(validatedSport)
                .level(request.getLevel())
                .description(request.getDescription())
                .city(request.getCity())
                .logo(request.getLogo())
                .status(TeamStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .createdBy(creator)
                .build();

        team = teamRepository.save(team);

        return mapToTeamResponse(team, authenticatedUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public TeamResponse getTeamById(Long teamId, Long authenticatedUserId) {
        Team team = findTeamOrThrow(teamId);
        return mapToTeamResponse(team, authenticatedUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamResponse> getAllTeams(String sport, String city, String level) {
        List<Team> teams;

        boolean hasSport = sport != null && !sport.isBlank();
        boolean hasCity  = city  != null && !city.isBlank();
        boolean hasLevel = level != null && !level.isBlank();

        if (hasSport && hasCity && hasLevel) {
            teams = teamRepository.findBySportAndCityAndLevelAndStatus(sport, city, level, TeamStatus.ACTIVE);
        } else if (hasSport && hasCity) {
            teams = teamRepository.findBySportAndCityAndStatus(sport, city, TeamStatus.ACTIVE);
        } else if (hasSport && hasLevel) {
            teams = teamRepository.findBySportAndLevelAndStatus(sport, level, TeamStatus.ACTIVE);
        } else if (hasCity && hasLevel) {
            teams = teamRepository.findByCityAndLevelAndStatus(city, level, TeamStatus.ACTIVE);
        } else if (hasSport) {
            teams = teamRepository.findBySportAndStatus(sport, TeamStatus.ACTIVE);
        } else if (hasCity) {
            teams = teamRepository.findByCityAndStatus(city, TeamStatus.ACTIVE);
        } else if (hasLevel) {
            teams = teamRepository.findByLevelAndStatus(level, TeamStatus.ACTIVE);
        } else {
            teams = teamRepository.findByStatus(TeamStatus.ACTIVE);
        }

        return teams.stream()
                .map(t -> mapToTeamResponse(t, null))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TeamResponse updateTeam(Long teamId, TeamRequest request, Long authenticatedUserId) {
        Team team = findTeamOrThrow(teamId);
        assertIsCaptain(teamId, authenticatedUserId);

        if (request.getName() != null && !request.getName().isBlank()) {
            team.setName(request.getName());
        }
        if (request.getSport() != null && !request.getSport().isBlank()) {
            team.setSport(resolveSportCategoryOrThrow(request.getSport()));
        }
        if (request.getLevel() != null)      team.setLevel(request.getLevel());
        if (request.getDescription() != null) team.setDescription(request.getDescription());
        if (request.getCity() != null)       team.setCity(request.getCity());
        if (request.getLogo() != null)       team.setLogo(request.getLogo());

        team = teamRepository.save(team);
        return mapToTeamResponse(team, authenticatedUserId);
    }

    @Override
    @Transactional
    public void deleteTeam(Long teamId, Long authenticatedUserId) {
        Team team = findTeamOrThrow(teamId);
        assertIsCaptain(teamId, authenticatedUserId);

        team.setStatus(TeamStatus.ARCHIVED);
        teamRepository.save(team);
    }

    // ──────────────────────────────────────────────
    // MEMBERS
    // ──────────────────────────────────────────────

    @Override
    @Transactional(readOnly = true)
    public List<TeamMemberResponse> getTeamMembers(Long teamId) {
        findTeamOrThrow(teamId);
        return teamMemberRepository.findAllByTeamIdAndStatus(teamId, MemberStatus.ACTIVE)
                .stream()
                .map(this::mapToMemberResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void leaveTeam(Long teamId, Long authenticatedUserId) {
        findTeamOrThrow(teamId);

        TeamMember member = teamMemberRepository
                .findByTeamIdAndUserIdAndStatus(teamId, authenticatedUserId, MemberStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("You are not an active member of this team"));

        boolean isCaptain = member.getTeamRole() == TeamRole.CAPTAIN;

        if (isCaptain) {
            long activeCount = teamMemberRepository.countByTeamIdAndStatus(teamId, MemberStatus.ACTIVE);
            if (activeCount == 1) {
                // Only member left → archive team
                Team team = findTeamOrThrow(teamId);
                team.setStatus(TeamStatus.ARCHIVED);
                teamRepository.save(team);
                member.setStatus(MemberStatus.LEFT);
                teamMemberRepository.save(member);
            } else {
                throw new ForbiddenException("You are the captain. Transfer captain role before leaving.");
            }
        } else {
            member.setStatus(MemberStatus.LEFT);
            teamMemberRepository.save(member);
        }
    }

    @Override
    @Transactional
    public void removeMember(Long teamId, Long targetUserId, Long authenticatedUserId) {
        findTeamOrThrow(teamId);
        assertIsCaptain(teamId, authenticatedUserId);

        if (targetUserId.equals(authenticatedUserId)) {
            throw new IllegalArgumentException("You cannot remove yourself. Use the leave endpoint instead.");
        }

        TeamMember target = teamMemberRepository
                .findByTeamIdAndUserIdAndStatus(teamId, targetUserId, MemberStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Target user is not an active member of this team"));

        if (target.getTeamRole() == TeamRole.CAPTAIN) {
            throw new ForbiddenException("Cannot remove the captain.");
        }

        target.setStatus(MemberStatus.REMOVED);
        teamMemberRepository.save(target);
    }

    @Override
    @Transactional
    public void changeMemberRole(Long teamId, Long targetUserId, ChangeMemberRoleRequest request, Long authenticatedUserId) {
        findTeamOrThrow(teamId);
        assertIsCaptain(teamId, authenticatedUserId);

        TeamMember target = teamMemberRepository
                .findByTeamIdAndUserIdAndStatus(teamId, targetUserId, MemberStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("Target user is not an active member of this team"));

        TeamRole newRole = request.getNewRole();

        // If promoting to CAPTAIN, demote current captain to CO_CAPTAIN
        if (newRole == TeamRole.CAPTAIN) {
            TeamMember currentCaptain = teamMemberRepository
                    .findByTeamIdAndUserIdAndStatus(teamId, authenticatedUserId, MemberStatus.ACTIVE)
                    .orElseThrow(() -> new ResourceNotFoundException("Current captain not found"));
            currentCaptain.setTeamRole(TeamRole.CO_CAPTAIN);
            teamMemberRepository.save(currentCaptain);
        }

        target.setTeamRole(newRole);
        teamMemberRepository.save(target);
    }

    // ──────────────────────────────────────────────
    // JOIN REQUESTS
    // ──────────────────────────────────────────────

    @Override
    @Transactional
    public void requestToJoin(Long teamId, JoinRequestRequest request, Long authenticatedUserId) {
        Team team = findTeamOrThrow(teamId);

        if (team.getStatus() != TeamStatus.ACTIVE) {
            throw new IllegalArgumentException("Cannot join an archived team.");
        }

        if (teamMemberRepository.existsByTeamIdAndUserIdAndStatus(teamId, authenticatedUserId, MemberStatus.ACTIVE)) {
            throw new ConflictException("You are already an active member of this team.");
        }

        if (joinRequestRepository.existsByTeamIdAndUserIdAndStatus(teamId, authenticatedUserId, JoinRequestStatus.PENDING)) {
            throw new ConflictException("You already have a pending join request for this team.");
        }

        User user = findUserOrThrow(authenticatedUserId);

        TeamJoinRequest joinRequest = TeamJoinRequest.builder()
                .team(team)
                .user(user)
                .message(request.getMessage())
                .status(JoinRequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        joinRequestRepository.save(joinRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<JoinRequestResponse> getJoinRequests(Long teamId, Long authenticatedUserId) {
        findTeamOrThrow(teamId);
        assertCanViewJoinRequests(teamId, authenticatedUserId);

        return joinRequestRepository.findAllByTeamIdOrderByCreatedAtDesc(teamId)
                .stream()
                .map(this::mapToJoinRequestResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void handleJoinRequest(Long teamId, Long requestId, JoinRequestActionRequest action, Long authenticatedUserId) {
        findTeamOrThrow(teamId);
        assertIsAdmin(authenticatedUserId);

        TeamJoinRequest joinRequest = joinRequestRepository.findByIdAndTeamId(requestId, teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Join request not found"));

        JoinRequestStatus newStatus = action.getStatus();

        if (newStatus != JoinRequestStatus.APPROVED && newStatus != JoinRequestStatus.REJECTED) {
            throw new IllegalArgumentException("Action must be APPROVED or REJECTED.");
        }

        if (joinRequest.getStatus() != JoinRequestStatus.PENDING) {
            if (joinRequest.getStatus() == newStatus) {
                return;
            }
            throw new IllegalArgumentException("This request has already been processed.");
        }

        User reviewer = findUserOrThrow(authenticatedUserId);
        joinRequest.setStatus(newStatus);
        joinRequest.setReviewedAt(LocalDateTime.now());
        joinRequest.setReviewedBy(reviewer);
        joinRequestRepository.save(joinRequest);

        if (newStatus == JoinRequestStatus.APPROVED) {
            TeamRole selectedRole = action.getTeamRole() != null ? action.getTeamRole() : TeamRole.PLAYER;

            if (selectedRole == TeamRole.CAPTAIN) {
                teamMemberRepository.findByTeamAndTeamRole(joinRequest.getTeam(), TeamRole.CAPTAIN)
                        .ifPresent(existingCaptain -> {
                            existingCaptain.setTeamRole(TeamRole.CO_CAPTAIN);
                            teamMemberRepository.save(existingCaptain);
                        });
            }

            TeamMember newMember = TeamMember.builder()
                    .team(joinRequest.getTeam())
                    .user(joinRequest.getUser())
                    .teamRole(selectedRole)
                    .status(MemberStatus.ACTIVE)
                    .joinedAt(LocalDateTime.now())
                    .build();
            teamMemberRepository.save(newMember);

            SportCommunity community = findOrCreateSportCommunity(joinRequest.getTeam().getSport());
            if (!communityMemberRepository.existsByCommunityIdAndUserId(community.getId(), joinRequest.getUser().getId())) {
                CommunityMember communityMember = CommunityMember.builder()
                        .community(community)
                        .user(joinRequest.getUser())
                        .joinedAt(LocalDateTime.now())
                        .role(CommunityMemberRole.MEMBER)
                        .build();
                communityMemberRepository.save(communityMember);
            }
        }
    }

    // ──────────────────────────────────────────────
    // HELPERS / MAPPING
    // ──────────────────────────────────────────────

    private Team findTeamOrThrow(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private String resolveSportCategoryOrThrow(String sportName) {
        if (sportName == null || sportName.isBlank()) {
            throw new IllegalArgumentException("Sport category is required.");
        }

        return categoryRepository.findByNomIgnoreCase(sportName.trim())
                .map(Category::getNom)
                .orElseThrow(() -> new ResourceNotFoundException("Sport category not found: " + sportName));
    }

            private SportCommunity findOrCreateSportCommunity(String sportName) {
            Category category = categoryRepository.findByNomIgnoreCase(sportName)
                .orElseThrow(() -> new ResourceNotFoundException("Sport category not found: " + sportName));

            return sportCommunityRepository.findBySportCategoryId(category.getId())
                .orElseGet(() -> sportCommunityRepository.save(
                    SportCommunity.builder()
                        .name(category.getNom() + " Community")
                        .sportCategory(category)
                        .createdAt(LocalDateTime.now())
                        .build()
                ));
            }

    private void assertIsCaptain(Long teamId, Long userId) {
        boolean isCaptain = teamMemberRepository
                .findByTeamIdAndUserIdAndStatus(teamId, userId, MemberStatus.ACTIVE)
                .map(m -> m.getTeamRole() == TeamRole.CAPTAIN)
                .orElse(false);
        if (!isCaptain) {
            throw new ForbiddenException("Only the team captain can perform this action.");
        }
    }

    private void assertIsCaptainOrCoCaptain(Long teamId, Long userId) {
        boolean isAuthorized = teamMemberRepository
                .findByTeamIdAndUserIdAndStatus(teamId, userId, MemberStatus.ACTIVE)
                .map(m -> m.getTeamRole() == TeamRole.CAPTAIN || m.getTeamRole() == TeamRole.CO_CAPTAIN)
                .orElse(false);
        if (!isAuthorized) {
            throw new ForbiddenException("Only the captain or co-captain can perform this action.");
        }
    }

    private void assertCanViewJoinRequests(Long teamId, Long userId) {
        if (isAdmin(userId)) {
            return;
        }
        assertIsCaptainOrCoCaptain(teamId, userId);
    }

    private void assertIsAdmin(Long userId) {
        User user = findUserOrThrow(userId);
        if (user.getRole() != Role.ROLE_ADMIN) {
            throw new ForbiddenException("Only admin can approve or reject join requests.");
        }
    }

    private boolean isAdmin(Long userId) {
        return userRepository.findById(userId)
                .map(u -> u.getRole() == Role.ROLE_ADMIN)
                .orElse(false);
    }

    private TeamResponse mapToTeamResponse(Team team, Long authenticatedUserId) {
        List<TeamMember> activeMembers = teamMemberRepository
                .findAllByTeamIdAndStatus(team.getId(), MemberStatus.ACTIVE);

        User creator = team.getCreatedBy();

        TeamCurrentUserContext context = null;
        if (authenticatedUserId != null) {
            boolean isMember = activeMembers.stream()
                    .anyMatch(m -> m.getUser().getId().equals(authenticatedUserId));
            boolean isCaptain = activeMembers.stream()
                    .anyMatch(m -> m.getUser().getId().equals(authenticatedUserId)
                            && m.getTeamRole() == TeamRole.CAPTAIN);
            boolean hasPending = joinRequestRepository
                    .existsByTeamIdAndUserIdAndStatus(team.getId(), authenticatedUserId, JoinRequestStatus.PENDING);
            context = new TeamCurrentUserContext(isMember, isCaptain, hasPending);
        }

        return TeamResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .sport(team.getSport())
                .level(team.getLevel())
                .description(team.getDescription())
                .city(team.getCity())
                .logo(team.getLogo())
                .status(team.getStatus())
                .createdAt(team.getCreatedAt())
                .captainId(creator != null ? creator.getId() : null)
                .captainFirstName(creator != null ? creator.getFirstName() : null)
                .captainLastName(creator != null ? creator.getLastName() : null)
                .membersCount(activeMembers.size())
                .currentUserContext(context)
                .build();
    }

    private TeamMemberResponse mapToMemberResponse(TeamMember member) {
        User user = member.getUser();
        return TeamMemberResponse.builder()
                .memberId(member.getId())
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .teamRole(member.getTeamRole())
                .joinedAt(member.getJoinedAt())
                .status(member.getStatus())
                .build();
    }

    private JoinRequestResponse mapToJoinRequestResponse(TeamJoinRequest jr) {
        User user = jr.getUser();
        User reviewer = jr.getReviewedBy();
        return JoinRequestResponse.builder()
                .id(jr.getId())
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .message(jr.getMessage())
                .status(jr.getStatus())
                .createdAt(jr.getCreatedAt())
                .reviewedAt(jr.getReviewedAt())
                .reviewedById(reviewer != null ? reviewer.getId() : null)
                .reviewedByName(reviewer != null ? reviewer.getFirstName() + " " + reviewer.getLastName() : null)
                .build();
    }
}