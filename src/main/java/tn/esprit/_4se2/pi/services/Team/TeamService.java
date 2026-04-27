package tn.esprit._4se2.pi.services.Team;

import lombok.RequiredArgsConstructor;
<<<<<<< Updated upstream
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.Enum.*;
import tn.esprit._4se2.pi.dto.*;
import tn.esprit._4se2.pi.entities.*;
import tn.esprit._4se2.pi.exception.*;
import tn.esprit._4se2.pi.repositories.*;
=======
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import tn.esprit._4se2.pi.Enum.Role;
import tn.esprit._4se2.pi.Enum.TeamStatus;
import tn.esprit._4se2.pi.entities.Category;
import tn.esprit._4se2.pi.entities.Team;
import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.repositories.CategoryRepository;
import tn.esprit._4se2.pi.repositories.MessageRepository;
import tn.esprit._4se2.pi.repositories.TeamJoinRequestRepository;
import tn.esprit._4se2.pi.repositories.TeamMemberRepository;
import tn.esprit._4se2.pi.repositories.TeamRepository;
import tn.esprit._4se2.pi.repositories.UserRepository;
>>>>>>> Stashed changes

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
<<<<<<< Updated upstream
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
=======
@Transactional
@Slf4j
public class TeamService implements TeamServiceInterface {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TeamJoinRequestRepository teamJoinRequestRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final MessageRepository messageRepository;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Team create(Team team) {
        if (team.getName() == null || team.getName().isBlank()) {
            throw new IllegalArgumentException("Team name is required");
        }

        if (team.getStatus() == null) {
            team.setStatus(TeamStatus.ACTIVE);
        }

        if (team.getCreatedAt() == null) {
            team.setCreatedAt(LocalDateTime.now());
        }

        if (team.getCreatedBy() == null) {
            resolveCurrentUser().ifPresent(team::setCreatedBy);
        }

        if (team.getCreatedBy() == null) {
            throw new IllegalArgumentException("Authenticated user is required to create a team");
        }

        return teamRepository.save(team);
>>>>>>> Stashed changes
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

<<<<<<< Updated upstream
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
=======
        existing.setName(updatedTeam.getName());
        existing.setSport(updatedTeam.getSport());
        existing.setLevel(updatedTeam.getLevel());
        existing.setDescription(updatedTeam.getDescription());
        existing.setCity(updatedTeam.getCity());
        existing.setLogo(updatedTeam.getLogo());
        if (updatedTeam.getCategory() != null && updatedTeam.getCategory().getId() != null) {
            Category category = categoryRepository.findById(updatedTeam.getCategory().getId()).orElse(null);
            existing.setCategory(category);
        }
        if (updatedTeam.getStatus() != null) {
            existing.setStatus(updatedTeam.getStatus());
        }

        return teamRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        if (!teamRepository.existsById(id)) {
            return;
        }

        // Explicitly delete dependent rows to satisfy FK constraints in legacy schema.
        messageRepository.deleteByTeam_Id(id);
        teamJoinRequestRepository.deleteByTeam_Id(id);
        teamMemberRepository.deleteByTeam_Id(id);
        deleteLegacyTeamMembersRows(id);

        teamRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Team> searchByCategoryAndOwnerKeyword(String categoryKeyword, String ownerKeyword) {
        String safeCategoryKeyword = categoryKeyword == null ? "" : categoryKeyword.trim();
        String safeOwnerKeyword = ownerKeyword == null ? "" : ownerKeyword.trim();
        return teamRepository.findDistinctByCategory_NomContainingIgnoreCaseAndCreatedBy_FirstNameContainingIgnoreCase(
                safeCategoryKeyword,
                safeOwnerKeyword
        );
    }

    @Override
    public int archiveDormantTeams(int olderThanDays) {
        int safeDays = Math.max(1, olderThanDays);
        LocalDateTime cutoff = LocalDateTime.now().minusDays(safeDays);
        int archived = teamRepository.archiveDormantTeams(cutoff);
        if (archived > 0) {
            log.info("Archived {} dormant team(s) older than {} days", archived, safeDays);
        }
        return archived;
    }

    private void deleteLegacyTeamMembersRows(Long teamId) {
        if (teamId == null || !tableExists("team_members")) {
            return;
        }
        jdbcTemplate.update("DELETE FROM team_members WHERE team_id = ?", teamId);
    }

    private boolean tableExists(String tableName) {
        Integer count = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM information_schema.TABLES
                WHERE TABLE_SCHEMA = DATABASE()
                  AND TABLE_NAME = ?
                """, Integer.class, tableName);
        return count != null && count > 0;
    }

    private boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }

        if (authentication.getAuthorities() != null && authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()))) {
            return true;
        }

        String email = authentication.getName();
        if (email == null || email.isBlank()) {
            return false;
        }

        return userRepository.findByEmail(email)
                .map(user -> user.getRole() == Role.ROLE_ADMIN)
                .orElse(false);
    }

    private java.util.Optional<User> resolveCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return java.util.Optional.empty();
        }
        String email = authentication.getName();
        if (email == null || email.isBlank()) {
            return java.util.Optional.empty();
        }
        return userRepository.findByEmail(email);
>>>>>>> Stashed changes
    }
}