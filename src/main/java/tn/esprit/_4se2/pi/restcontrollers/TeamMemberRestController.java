package tn.esprit._4se2.pi.restcontrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.services.TeamMember.ITeamMemberService;
import tn.esprit._4se2.pi.entities.TeamMember;

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

    @GetMapping("/{id}")
    public ResponseEntity<TeamMember> getTeamMemberById(@PathVariable Long id) {
        return ResponseEntity.ok(teamMemberService.getTeamMemberById(id));
    }

    @PutMapping
    public ResponseEntity<TeamMember> updateTeamMember(@RequestBody TeamMember teamMember) {
        return ResponseEntity.ok(teamMemberService.updateTeamMember(teamMember));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeamMember(@PathVariable Long id) {
        teamMemberService.deleteTeamMember(id);
        return ResponseEntity.noContent().build();
    }
}
