package tn.esprit._4se2.pi.restcontrollers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.entities.Advertisement;
import tn.esprit._4se2.pi.services.AdvertisementService;

import java.util.List;

@RestController
@RequestMapping("/api/advertisements")
@CrossOrigin(origins = "*")
public class AdvertisementController {

    private final AdvertisementService service;

    public AdvertisementController(AdvertisementService service) {
        this.service = service;
    }

    @PostMapping
    public Advertisement create(@RequestBody Advertisement ad) {
        return service.create(ad);
    }

    @GetMapping
    public List<Advertisement> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Advertisement getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public Advertisement update(@PathVariable Long id, @RequestBody Advertisement ad) {
        return service.update(id, ad);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    @PostMapping("/{id}/click")
    public ResponseEntity<Void> click(@PathVariable Long id) {
        service.incrementClicks(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/impression")
    public ResponseEntity<Void> impression(@PathVariable Long id) {
        service.incrementImpressions(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/competition/{competitionId}")
    public List<Advertisement> getByCompetition(@PathVariable Long competitionId) {
        return service.getByCompetition(competitionId);
    }

    @GetMapping("/match/{matchId}")
    public List<Advertisement> getByMatch(@PathVariable Long matchId) {
        return service.getByMatch(matchId);
    }

    @GetMapping("/team/{teamId}")
    public List<Advertisement> getByTeam(@PathVariable Long teamId) {
        return service.getByTeam(teamId);
    }
}
