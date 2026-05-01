package tn.esprit._4se2.pi.restcontrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.dto.SportSpace.SportSpaceRequest;
import tn.esprit._4se2.pi.dto.SportSpace.SportSpaceResponse;
import tn.esprit._4se2.pi.dto.SportSpace.SportspacestatsResponse;
import tn.esprit._4se2.pi.services.SportSpace.ISportSpaceService;
import jakarta.validation.Valid;
import tn.esprit._4se2.pi.services.SportSpace.SportSpaceService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/sport-spaces")
@RequiredArgsConstructor
public class SportSpaceRestController {

    private final ISportSpaceService sportSpaceService;

    @PostMapping
    public ResponseEntity<SportSpaceResponse> createSportSpace(@Valid @RequestBody SportSpaceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sportSpaceService.createSportSpace(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SportSpaceResponse> getSportSpaceById(@PathVariable Long id) {
        return ResponseEntity.ok(sportSpaceService.getSportSpaceById(id));
    }

    @GetMapping
    public ResponseEntity<List<SportSpaceResponse>> getAllSportSpaces() {
        return ResponseEntity.ok(sportSpaceService.getAllSportSpaces());
    }

    @GetMapping("/owner/{fieldOwnerId}")
    public ResponseEntity<List<SportSpaceResponse>> getSportSpacesByFieldOwnerId(@PathVariable Long fieldOwnerId) {
        return ResponseEntity.ok(sportSpaceService.getSportSpacesByFieldOwnerId(fieldOwnerId));
    }

    @GetMapping("/owner/me")
    public ResponseEntity<List<SportSpaceResponse>> getMySportSpaces(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(sportSpaceService.getSportSpacesByFieldOwnerEmail(authentication.getName()));
    }

    @GetMapping("/type/{sportType}")
    public ResponseEntity<List<SportSpaceResponse>> getSportSpacesBySportType(@PathVariable String sportType) {
        return ResponseEntity.ok(sportSpaceService.getSportSpacesBySportType(sportType));
    }

    @GetMapping("/available")
    public ResponseEntity<List<SportSpaceResponse>> getAvailableSportSpaces() {
        return ResponseEntity.ok(sportSpaceService.getAvailableSportSpaces());
    }

    @GetMapping("/search")
    public ResponseEntity<List<SportSpaceResponse>> searchSportSpacesByLocation(@RequestParam String location) {
        return ResponseEntity.ok(sportSpaceService.searchSportSpacesByLocation(location));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SportSpaceResponse> updateSportSpace(
            @PathVariable Long id,
            @Valid @RequestBody SportSpaceRequest request) {
        return ResponseEntity.ok(sportSpaceService.updateSportSpace(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSportSpace(@PathVariable Long id) {
        sportSpaceService.deleteSportSpace(id);
        return ResponseEntity.noContent().build();
    }

    // ── TÂCHE 2 : endpoint JPQL stats ─────────────────────────────────────────
    /**
     * GET /api/sport-spaces/stats
     * Retourne les terrains disponibles avec leur note moyenne (feedbacks APPROVED)
     * et leur nombre de réservations, calculés via une requête JPQL avec JOIN.
     */
    @GetMapping("/stats")
    public ResponseEntity<List<SportspacestatsResponse>> getAvailableSpacesWithStats() {
        return ResponseEntity.ok(sportSpaceService.getAvailableSpacesWithStats());
    }

    @GetMapping("/filter")
    public ResponseEntity<List<SportSpaceResponse>> filterSpaces(
            @RequestParam String sportType,
            @RequestParam BigDecimal maxRate) {
        return ResponseEntity.ok(
                ((SportSpaceService) sportSpaceService)
                        .filterBySportTypeAndMaxRate(sportType, maxRate));
    }

    @GetMapping("/sportSpaces/avgFeedbackAndBookings")
    public List<SportSpaceResponse> getAllSportSpacesWithStats() {
        return sportSpaceService.getAllSportSpaces();
    }
}
