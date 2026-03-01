package tn.esprit._4se2.pi.restcontrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.dto.SportSpaceAvailabilityRequest;
import tn.esprit._4se2.pi.dto.SportSpaceAvailabilityResponse;
import tn.esprit._4se2.pi.services.SportSpaceAvailability.ISportSpaceAvailabilityService;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/availabilities")
@RequiredArgsConstructor
public class SportSpaceAvailabilityRestController {

    private final ISportSpaceAvailabilityService availabilityService;

    @PostMapping
    public ResponseEntity<SportSpaceAvailabilityResponse> createAvailability(
            @Valid @RequestBody SportSpaceAvailabilityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(availabilityService.createAvailability(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SportSpaceAvailabilityResponse> getAvailabilityById(@PathVariable Long id) {
        return ResponseEntity.ok(availabilityService.getAvailabilityById(id));
    }

    @GetMapping
    public ResponseEntity<List<SportSpaceAvailabilityResponse>> getAllAvailabilities() {
        return ResponseEntity.ok(availabilityService.getAllAvailabilities());
    }

    @GetMapping("/sport-space/{sportSpaceId}")
    public ResponseEntity<List<SportSpaceAvailabilityResponse>> getAvailabilitiesBySportSpaceId(
            @PathVariable Long sportSpaceId) {
        return ResponseEntity.ok(availabilityService.getAvailabilitiesBySportSpaceId(sportSpaceId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SportSpaceAvailabilityResponse> updateAvailability(
            @PathVariable Long id,
            @Valid @RequestBody SportSpaceAvailabilityRequest request) {
        return ResponseEntity.ok(availabilityService.updateAvailability(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAvailability(@PathVariable Long id) {
        availabilityService.deleteAvailability(id);
        return ResponseEntity.noContent().build();
    }
}