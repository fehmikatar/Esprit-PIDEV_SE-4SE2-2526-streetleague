package tn.esprit._4se2.pi.Controllers;

import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.Entities.Match;
import tn.esprit._4se2.pi.Services.MatchService;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchController {
    private final MatchService matchService;

    public MatchController(MatchService matchService) {
        this.matchService = matchService;
    }

    @PostMapping
    public Match create(@RequestBody Match match) {
        return matchService.create(match);
    }

    @GetMapping
    public List<Match> getAll(@RequestParam(required = false) Long competitionId) {
        return matchService.getAll(competitionId);
    }

    @GetMapping("/{id}")
    public Match getById(@PathVariable Long id) {
        return matchService.getById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        matchService.delete(id);
    }

}
