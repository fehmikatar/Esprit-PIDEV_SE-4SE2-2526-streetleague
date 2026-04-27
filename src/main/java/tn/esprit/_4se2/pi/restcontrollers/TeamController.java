package tn.esprit._4se2.pi.restcontrollers;

import org.springframework.web.bind.annotation.*;
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

    // Créer une équipe
    @PostMapping
    public Team create(@RequestBody Team team, @RequestParam(required = false) Long userId) {
        return teamService.create(team, userId);
    }

    // Récupérer toutes les équipes
    @GetMapping
    public List<Team> getAll() {
        return teamService.getAll();
    }

    // Récupérer une équipe par son ID
    @GetMapping("/{id}")
    public Team getById(@PathVariable Long id) {
        return teamService.getById(id);
    }

    // Récupérer une équipe par son nom
    @GetMapping("/name/{name}")
    public Team getByName(@PathVariable String name) {
        return teamService.getByName(name);
    }

    // Mettre à jour une équipe
    @PutMapping("/{id}")
    public Team update(@PathVariable Long id, @RequestBody Team team) {
        return teamService.update(id, team);
    }

    // Supprimer une équipe
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        teamService.delete(id);
    }
}
