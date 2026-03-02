package tn.esprit._4se2.pi.restcontrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.dto.SportSpaceRequest;
import tn.esprit._4se2.pi.dto.SportSpaceResponse;
import tn.esprit._4se2.pi.services.SportSpace.ISportSpaceService;
import jakarta.validation.Valid;
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
}