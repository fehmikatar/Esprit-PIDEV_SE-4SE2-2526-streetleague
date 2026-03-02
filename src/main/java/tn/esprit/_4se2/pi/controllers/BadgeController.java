package tn.esprit._4se2.pi.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.entities.Badge;
import tn.esprit._4se2.pi.services.BadgeService;

import java.util.List;

@RestController
@RequestMapping("/api/badges")
@RequiredArgsConstructor // Lombok : constructeur pour l'injection du service
public class BadgeController {

    private final BadgeService badgeService;

    // Create
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Badge createBadge(@RequestBody Badge badge) {
        return badgeService.createBadge(badge);
    }

    // Read all
    @GetMapping
    public List<Badge> getAllBadges() {
        return badgeService.getAllBadges();
    }

    // Read by id
    @GetMapping("/{id}")
    public Badge getBadgeById(@PathVariable Long id) {
        return badgeService.getBadgeById(id);
    }

    // Update
    @PutMapping("/{id}")
    public Badge updateBadge(@PathVariable Long id, @RequestBody Badge badge) {
        return badgeService.updateBadge(id, badge);
    }

    // Delete
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBadge(@PathVariable Long id) {
        badgeService.deleteBadge(id);
    }
}