package tn.esprit._4se2.pi.Controllers;

import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.Entities.Competition;
import tn.esprit._4se2.pi.Services.CompetitionService;

import java.util.List;

@RestController
@RequestMapping("/api/competitions")
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
}
