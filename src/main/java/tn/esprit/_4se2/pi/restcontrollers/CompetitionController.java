package tn.esprit._4se2.pi.restcontrollers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.dto.Competition.CompetitionAnalyticsDto;
import tn.esprit._4se2.pi.dto.Competition.SeedingRequestDto;
import tn.esprit._4se2.pi.dto.Competition.SeedingResultDto;
import tn.esprit._4se2.pi.entities.Competition;
import tn.esprit._4se2.pi.services.Competition.CompetitionService;

import java.util.List;

@RestController
@RequestMapping("/api/competitions")
@CrossOrigin(origins = "*")
public class CompetitionController {
    private final CompetitionService competitionService;

    public CompetitionController(CompetitionService competitionService) {
        this.competitionService = competitionService;
    }

    @PostMapping
    public Competition create(@RequestBody Competition competition) {
        return competitionService.create(competition);
    }

    @GetMapping
    public List<Competition> getAll() {
        return competitionService.getAll();
    }

    @GetMapping("/{id}")
    public Competition getById(@PathVariable Long id) {
        return competitionService.getById(id);
    }

    @PutMapping("/{id}")
    public Competition update(@PathVariable Long id, @RequestBody Competition competition) {
        return competitionService.update(id, competition);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        competitionService.delete(id);
    }

    // ─── MÉTIER 1 — Seeding Engine ────────────────────────────────────────────
    /**
     * strategy = ELO | WIN_RATE | MANUAL
     * For MANUAL, provide body: { "seeds": [{ "teamId": 1, "seed": 1 }, ...] }
     */
    @PostMapping("/{id}/seeding")
    public ResponseEntity<List<SeedingResultDto>> computeSeeding(
            @PathVariable Long id,
            @RequestParam(defaultValue = "WIN_RATE") String strategy,
            @RequestBody(required = false) SeedingRequestDto body) {
        List<SeedingResultDto> result = competitionService.computeSeeding(id, strategy, body);
        return ResponseEntity.ok(result);
    }

    // ─── MÉTIER 2 — Competition Analytics ────────────────────────────────────
    @GetMapping("/{id}/analytics")
    public ResponseEntity<CompetitionAnalyticsDto> getAnalytics(@PathVariable Long id) {
        return ResponseEntity.ok(competitionService.getAnalytics(id));
    }
}
