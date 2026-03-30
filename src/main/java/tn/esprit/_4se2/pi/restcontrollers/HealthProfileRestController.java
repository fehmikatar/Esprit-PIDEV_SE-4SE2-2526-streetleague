package tn.esprit._4se2.pi.restcontrollers;

import io.swagger.v3.oas.annotations.tags.Tag;
import tn.esprit._4se2.pi.entities.FitnessStatus;
import tn.esprit._4se2.pi.entities.HealthProfile;
import tn.esprit._4se2.pi.dto.HealthProfileRequest;
import tn.esprit._4se2.pi.dto.HealthProfileResponse;
import tn.esprit._4se2.pi.services.IHealthProfileService;
import tn.esprit._4se2.pi.mappers.HealthProfileMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/health-profiles")
@RequiredArgsConstructor
@Tag(name = "Health Profiles", description = "📊 Patient health profiles")
public class HealthProfileRestController {

    private final IHealthProfileService healthProfileService;
    private final HealthProfileMapper healthProfileMapper;

    @GetMapping
    public ResponseEntity<List<HealthProfileResponse>> getAllHealthProfiles() {
        List<HealthProfile> profiles = healthProfileService.getAllHealthProfiles();
        List<HealthProfileResponse> responses = profiles.stream()
                .map(healthProfileMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HealthProfileResponse> getHealthProfileById(@PathVariable Long id) {
        HealthProfile profile = healthProfileService.getHealthProfileById(id);
        HealthProfileResponse response = healthProfileMapper.toResponse(profile);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<HealthProfileResponse> createHealthProfile(@RequestBody HealthProfileRequest request) {
        HealthProfile profile = healthProfileMapper.toEntity(request);
        HealthProfile created = healthProfileService.createHealthProfile(profile);
        HealthProfileResponse response = healthProfileMapper.toResponse(created);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HealthProfileResponse> updateHealthProfile(@PathVariable Long id, @RequestBody HealthProfileRequest request) {
        HealthProfile profile = healthProfileMapper.toEntity(request);
        HealthProfile updated = healthProfileService.updateHealthProfile(id, profile);
        HealthProfileResponse response = healthProfileMapper.toResponse(updated);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHealthProfile(@PathVariable Long id) {
        healthProfileService.deleteHealthProfile(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<HealthProfileResponse> getHealthProfileByUser(@PathVariable Long userId) {
        HealthProfile profile = healthProfileService.getHealthProfileByUser(userId);
        HealthProfileResponse response = healthProfileMapper.toResponse(profile);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/fitness-status/{status}")
    public ResponseEntity<List<HealthProfileResponse>> getByFitnessStatus(@PathVariable FitnessStatus status) {
        List<HealthProfile> profiles = healthProfileService.getHealthProfilesByFitnessStatus(status);
        List<HealthProfileResponse> responses = profiles.stream()
                .map(healthProfileMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}