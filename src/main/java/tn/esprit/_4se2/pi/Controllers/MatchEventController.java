package tn.esprit._4se2.pi.Controllers;

import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.Entities.MatchEvent;
import tn.esprit._4se2.pi.Services.MatchEventService;

import java.util.List;

@RestController
@RequestMapping("/api/match-events")
public class MatchEventController {
    private final MatchEventService matchEventService;

    public MatchEventController(MatchEventService matchEventService) {
        this.matchEventService = matchEventService;
    }

    @PostMapping
    public MatchEvent create(@RequestBody MatchEvent matchEvent) {
        return matchEventService.logEvent(matchEvent);
    }

    @GetMapping("/match/{matchId}")
    public List<MatchEvent> getByMatchId(@PathVariable Long matchId) {
        return matchEventService.getByMatchId(matchId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        matchEventService.delete(id);
    }

}
