package tn.esprit._4se2.pi.restcontrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.dto.HealthProfile.HealthProfileRequest;
import tn.esprit._4se2.pi.dto.HealthProfile.HealthProfileResponse;
import tn.esprit._4se2.pi.services.HealthProfile.IHealthProfileService;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/health-profiles")
@RequiredArgsConstructor
public class HealthProfileRestController {

    private final IHealthProfileService healthProfileService;

    @PostMapping
    public ResponseEntity<HealthProfileResponse> createHealthProfile(@Valid @RequestBody HealthProfileRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(healthProfileService.createHealthProfile(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HealthProfileResponse> getHealthProfileById(@PathVariable Long id) {
        return ResponseEntity.ok(healthProfileService.getHealthProfileById(id));
    }
    @GetMapping
    public ResponseEntity<List<HealthProfileResponse>> getAllHealthProfiles() {
        return ResponseEntity.ok(healthProfileService.getAllHealthProfiles());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<HealthProfileResponse> getHealthProfileByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(healthProfileService.getHealthProfileByUserId(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HealthProfileResponse> updateHealthProfile(
            @PathVariable Long id,
            @Valid @RequestBody HealthProfileRequest request) {
        return ResponseEntity.ok(healthProfileService.updateHealthProfile(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHealthProfile(@PathVariable Long id) {
        healthProfileService.deleteHealthProfile(id);
        return ResponseEntity.noContent().build();
    }
}