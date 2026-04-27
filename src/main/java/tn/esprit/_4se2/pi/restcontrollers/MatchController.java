package tn.esprit._4se2.pi.restcontrollers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.dto.Match.MatchRatingDto;
import tn.esprit._4se2.pi.dto.Match.ScheduleRequestDto;
import tn.esprit._4se2.pi.dto.Match.ScheduleResultDto;
import tn.esprit._4se2.pi.entities.Match;
import tn.esprit._4se2.pi.services.Match.MatchService;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
@CrossOrigin(origins = "*")
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

    @PutMapping("/{id}")
    public Match update(@PathVariable Long id, @RequestBody Match match) {
        return matchService.update(id, match);
    }

    @PutMapping("/{id}/live-score")
    public Match updateLiveScore(@PathVariable Long id,
                                  @RequestParam Integer homeScore,
                                  @RequestParam Integer awayScore) {
        return matchService.updateLiveScore(id, homeScore, awayScore);
    }

    @PostMapping("/{id}/finish")
    public Match finishMatch(@PathVariable Long id) {
        return matchService.finishMatch(id);
    }

    @PutMapping("/{id}/start")
    public Match startMatch(@PathVariable Long id) {
        return matchService.startMatch(id);
    }

    // ─── MÉTIER 1 — Performance Rating Engine ────────────────────────────────
    @GetMapping("/{id}/rating")
    public ResponseEntity<MatchRatingDto> getRating(@PathVariable Long id) {
        return ResponseEntity.ok(matchService.computeRating(id));
    }

    // ─── MÉTIER 2 — Match Scheduler ──────────────────────────────────────────
    @PostMapping("/competition/{competitionId}/schedule")
    public ResponseEntity<ScheduleResultDto> scheduleMatches(
            @PathVariable Long competitionId,
            @RequestBody ScheduleRequestDto request) {
        return ResponseEntity.ok(matchService.scheduleMatches(competitionId, request));
    }
}
