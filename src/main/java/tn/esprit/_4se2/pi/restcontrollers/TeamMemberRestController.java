package tn.esprit._4se2.pi.restcontrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.services.TeamMember.ITeamMemberService;
import tn.esprit._4se2.pi.entities.TeamMember;
import tn.esprit._4se2.pi.entities.TeamMemberId;

import java.util.List;

@RestController
@RequestMapping("/api/team-members")
@RequiredArgsConstructor
public class TeamMemberRestController {

    private final ITeamMemberService teamMemberService;

    @PostMapping
    public ResponseEntity<TeamMember> addTeamMember(@RequestBody TeamMember teamMember) {
        return ResponseEntity.ok(teamMemberService.addTeamMember(teamMember));
    }

    @GetMapping
    public ResponseEntity<List<TeamMember>> getAllTeamMembers() {
        return ResponseEntity.ok(teamMemberService.getAllTeamMembers());
    }

    @GetMapping("/{userId}/{teamId}")
    public ResponseEntity<TeamMember> getTeamMemberById(@PathVariable Long userId, @PathVariable Long teamId) {
        TeamMemberId id = new TeamMemberId(userId, teamId);
        return ResponseEntity.ok(teamMemberService.getTeamMemberById(id));
    }

    @PutMapping
    public ResponseEntity<TeamMember> updateTeamMember(@RequestBody TeamMember teamMember) {
        return ResponseEntity.ok(teamMemberService.updateTeamMember(teamMember));
    }

    @DeleteMapping("/{userId}/{teamId}")
    public ResponseEntity<Void> deleteTeamMember(@PathVariable Long userId, @PathVariable Long teamId) {
        TeamMemberId id = new TeamMemberId(userId, teamId);
        teamMemberService.deleteTeamMember(id);
        return ResponseEntity.noContent().build();
    }
}
