package tn.esprit._4se2.pi.restcontrollers;

import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.dto.Team.TeamRequest;
import tn.esprit._4se2.pi.dto.Team.TeamResponse;
import tn.esprit._4se2.pi.entities.Team;
import tn.esprit._4se2.pi.services.Team.TeamService;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
public class TeamController {


    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @PostMapping
    public TeamResponse create(@RequestBody TeamRequest request,
                               @RequestParam Long userId) {
        return teamService.createTeam(request, userId);
    }

    @GetMapping
    public List<TeamResponse> getAll(@RequestParam(required = false) String sport,
                                     @RequestParam(required = false) String city,
                                     @RequestParam(required = false) String level) {
        return teamService.getAllTeams(sport, city, level);
    }

    @GetMapping("/{id}")
    public TeamResponse getById(@PathVariable Long id,
                                @RequestParam(required = false) Long userId) {
        return teamService.getTeamById(id, userId);
    }

    @PutMapping("/{id}")
    public TeamResponse update(@PathVariable Long id,
                               @RequestBody TeamRequest request,
                               @RequestParam Long userId) {
        return teamService.updateTeam(id, request, userId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id,
                       @RequestParam Long userId) {
        teamService.deleteTeam(id, userId);
    }
}
