package tn.esprit._4se2.pi.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.dto.BadgeRequest;
import tn.esprit._4se2.pi.dto.BadgeResponse;
import tn.esprit._4se2.pi.services.IBadgeService;

import java.util.List;

@RestController
@RequestMapping("/api/badges")
@RequiredArgsConstructor
public class BadgeController {


    private final IBadgeService badgeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BadgeResponse createBadge(@Valid @RequestBody BadgeRequest request) {
        return badgeService.createBadge(request);
    }

    @GetMapping
    public List<BadgeResponse> getAllBadges() {
        return badgeService.getAllBadges();
    }

    @GetMapping("/{id}")
    public BadgeResponse getBadgeById(@PathVariable Long id) {
        return badgeService.getBadgeById(id);
    }

    @PutMapping("/{id}")
    public BadgeResponse updateBadge(@PathVariable Long id, @Valid @RequestBody BadgeRequest request) {
        return badgeService.updateBadge(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBadge(@PathVariable Long id) {
        badgeService.deleteBadge(id);
    }
}