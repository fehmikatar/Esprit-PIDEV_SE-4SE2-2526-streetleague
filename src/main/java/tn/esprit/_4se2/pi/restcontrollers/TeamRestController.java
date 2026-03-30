package tn.esprit._4se2.pi.restcontrollers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.dto.*;
import tn.esprit._4se2.pi.repositories.UserRepository;
import tn.esprit._4se2.pi.services.Team.ITeamService;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamRestController {

    private final ITeamService teamService;
    private final UserRepository userRepository;

    // ──────────────────────────────────────────────
    // TEAM CRUD
    // ──────────────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TeamResponse> createTeam(
            @Valid @RequestBody TeamRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = resolveUserId(userDetails);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(teamService.createTeam(request, userId));
    }

    @GetMapping
    public ResponseEntity<List<TeamResponse>> getAllTeams(
            @RequestParam(required = false) String sport,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String level) {
        return ResponseEntity.ok(teamService.getAllTeams(sport, city, level));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamResponse> getTeamById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = resolveUserId(userDetails);
        return ResponseEntity.ok(teamService.getTeamById(id, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamResponse> updateTeam(
            @PathVariable Long id,
            @Valid @RequestBody TeamRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = resolveUserId(userDetails);
        return ResponseEntity.ok(teamService.updateTeam(id, request, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = resolveUserId(userDetails);
        teamService.deleteTeam(id, userId);
        return ResponseEntity.noContent().build();
    }

    // ──────────────────────────────────────────────
    // MEMBERS
    // ──────────────────────────────────────────────

    @GetMapping("/{id}/members")
    public ResponseEntity<List<TeamMemberResponse>> getTeamMembers(@PathVariable Long id) {
        return ResponseEntity.ok(teamService.getTeamMembers(id));
    }

    @DeleteMapping("/{id}/members/me")
    public ResponseEntity<Void> leaveTeam(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = resolveUserId(userDetails);
        teamService.leaveTeam(id, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/members/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long id,
            @PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long requesterId = resolveUserId(userDetails);
        teamService.removeMember(id, userId, requesterId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/members/{userId}/role")
    public ResponseEntity<Void> changeMemberRole(
            @PathVariable Long id,
            @PathVariable Long userId,
            @Valid @RequestBody ChangeMemberRoleRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long requesterId = resolveUserId(userDetails);
        teamService.changeMemberRole(id, userId, request, requesterId);
        return ResponseEntity.ok().build();
    }

    // ──────────────────────────────────────────────
    // JOIN REQUESTS
    // ──────────────────────────────────────────────

    @PostMapping("/{id}/join-requests")
    public ResponseEntity<Void> requestToJoin(
            @PathVariable Long id,
            @RequestBody JoinRequestRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = resolveUserId(userDetails);
        teamService.requestToJoin(id, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}/join-requests")
    public ResponseEntity<List<JoinRequestResponse>> getJoinRequests(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = resolveUserId(userDetails);
        return ResponseEntity.ok(teamService.getJoinRequests(id, userId));
    }

    @PatchMapping("/{id}/join-requests/{requestId}")
    public ResponseEntity<Void> handleJoinRequest(
            @PathVariable Long id,
            @PathVariable Long requestId,
            @Valid @RequestBody JoinRequestActionRequest action,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = resolveUserId(userDetails);
        teamService.handleJoinRequest(id, requestId, action, userId);
        return ResponseEntity.ok().build();
    }

    // ──────────────────────────────────────────────
    // HELPER
    // ──────────────────────────────────────────────

    private Long resolveUserId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"))
                .getId();
    }
}
