package tn.esprit._4se2.pi.restcontrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.dto.TeamManagerRequest;
import tn.esprit._4se2.pi.dto.TeamManagerResponse;
import tn.esprit._4se2.pi.services.TeamManager.ITeamManagerService;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/team-managers")
@RequiredArgsConstructor
public class TeamManagerRestController {

    private final ITeamManagerService teamManagerService;

    @PostMapping
    public ResponseEntity<TeamManagerResponse> createTeamManager(@Valid @RequestBody TeamManagerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teamManagerService.createTeamManager(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamManagerResponse> getTeamManagerById(@PathVariable Long id) {
        return ResponseEntity.ok(teamManagerService.getTeamManagerById(id));
    }

    @GetMapping
    public ResponseEntity<List<TeamManagerResponse>> getAllTeamManagers() {
        return ResponseEntity.ok(teamManagerService.getAllTeamManagers());
    }

    @GetMapping("/code/{teamCode}")
    public ResponseEntity<TeamManagerResponse> getTeamManagerByTeamCode(@PathVariable String teamCode) {
        return ResponseEntity.ok(teamManagerService.getTeamManagerByTeamCode(teamCode));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamManagerResponse> updateTeamManager(
            @PathVariable Long id,
            @Valid @RequestBody TeamManagerRequest request) {
        return ResponseEntity.ok(teamManagerService.updateTeamManager(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeamManager(@PathVariable Long id) {
        teamManagerService.deleteTeamManager(id);
        return ResponseEntity.noContent().build();
    }
}