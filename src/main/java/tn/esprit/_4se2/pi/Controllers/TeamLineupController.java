package tn.esprit._4se2.pi.Controllers;

import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.Entities.TeamLineup;
import tn.esprit._4se2.pi.Services.TeamLineupService;

@RestController
@RequestMapping("/api/team-lineups")
public class TeamLineupController {
    private final TeamLineupService teamLineupService;

    public TeamLineupController(TeamLineupService teamLineupService) {
        this.teamLineupService = teamLineupService;
    }

    @PostMapping
    public TeamLineup create(@RequestBody TeamLineup teamLineup) {
        return teamLineupService.create(teamLineup);
    }

    @GetMapping("/match/{matchId}/team/{teamId}")
    public TeamLineup getByMatchAndTeam(@PathVariable Long matchId, @PathVariable Long teamId) {
        return teamLineupService.getByMatchAndTeam(matchId, teamId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        teamLineupService.delete(id);
    }
}
