package tn.esprit._4se2.pi.restcontrollers;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.entities.Team;
import tn.esprit._4se2.pi.entities.TeamJoinRequest;
import tn.esprit._4se2.pi.entities.TeamMember;
import tn.esprit._4se2.pi.repositories.TeamMemberRepository;
import tn.esprit._4se2.pi.services.Team.TeamJoinRequestService;
import tn.esprit._4se2.pi.services.Team.TeamService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final TeamJoinRequestService joinRequestService;
    private final TeamMemberRepository teamMemberRepository;

    @PostMapping
    public Team create(@RequestBody Team team, @RequestParam(required = false) Long userId) {
        return teamService.create(team, userId);
    }

    @GetMapping
    public List<Team> getAll() {
        return teamService.getAll();
    }

    @GetMapping("/{id}")
    public Team getById(@PathVariable Long id) {
        return teamService.getById(id);
    }

    @GetMapping("/name/{name}")
    public Team getByName(@PathVariable String name) {
        return teamService.getByName(name);
    }

    @PutMapping("/{id}")
    public Team update(@PathVariable Long id, @RequestBody Team team) {
        return teamService.update(id, team);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        teamService.delete(id);
    }

    // ===== ENDPOINTS POUR LES DEMANDES D'ADHÉSION =====

    @PostMapping("/{teamId}/join-requests")
    public ResponseEntity<TeamJoinRequest> requestJoinTeam(
            @PathVariable Long teamId,
            @RequestBody(required = false) Map<String, String> payload,
            @AuthenticationPrincipal UserDetails userDetails) {

        String message = payload != null ? payload.get("message") : null;
        String userEmail = userDetails.getUsername();

        TeamJoinRequest request = joinRequestService.createJoinRequest(teamId, userEmail, message);
        return ResponseEntity.ok(request);
    }

    @GetMapping("/join-requests")
    public ResponseEntity<List<TeamJoinRequest>> getJoinRequests(
            @AuthenticationPrincipal UserDetails userDetails) {

        String userEmail = userDetails.getUsername();
        List<TeamJoinRequest> requests = joinRequestService.getUserJoinRequests(userEmail);
        return ResponseEntity.ok(requests);
    }

    @PatchMapping("/join-requests/{requestId}/approve")
    public ResponseEntity<TeamJoinRequest> approveJoinRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String reviewerEmail = userDetails.getUsername();
        TeamJoinRequest approved = joinRequestService.approveJoinRequest(requestId, reviewerEmail);
        return ResponseEntity.ok(approved);
    }

    @PostMapping("/join-requests/{requestId}/reject")
    public ResponseEntity<TeamJoinRequest> rejectJoinRequest(
            @PathVariable Long requestId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String reviewerEmail = userDetails.getUsername();
        TeamJoinRequest rejected = joinRequestService.rejectJoinRequest(requestId, reviewerEmail);
        return ResponseEntity.ok(rejected);
    }

    @GetMapping("/{teamId}/members")
    public ResponseEntity<List<User>> getTeamMembers(@PathVariable Long teamId) {
        List<TeamMember> members = teamMemberRepository.findByTeamId(teamId);
        List<User> users = members.stream()
                .map(TeamMember::getUser)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    /**
     * Récupère les détails complets d'une équipe (pour le guard de chat)
     */
    @GetMapping("/{teamId}/details")
    public ResponseEntity<Team> getTeamDetails(@PathVariable Long teamId) {
        try {
            Team team = teamService.getTeamById(teamId);
            if (team == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(team);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}