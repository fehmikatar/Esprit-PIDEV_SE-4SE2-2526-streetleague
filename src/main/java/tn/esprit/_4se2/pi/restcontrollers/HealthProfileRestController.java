package tn.esprit._4se2.pi.restcontrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.dto.HealthProfile.ActivityRecommendationDto;
import tn.esprit._4se2.pi.dto.HealthProfile.HealthProfileRequest;
import tn.esprit._4se2.pi.dto.HealthProfile.HealthProfileResponse;
import tn.esprit._4se2.pi.services.HealthProfile.IHealthProfileService;
import jakarta.validation.Valid;

import tn.esprit._4se2.pi.dto.Athlet.AthleteRequest;
import tn.esprit._4se2.pi.dto.Athlet.ChatResponse;
import tn.esprit._4se2.pi.entities.HealthMetrics;
import java.util.Map;
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

    @GetMapping("/user/{userId}/activities")
    public ResponseEntity<List<ActivityRecommendationDto>> getActivityPlanByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(healthProfileService.generateActivityPlanByUserId(userId));
    }

    @GetMapping("/user/{userId}/activities/{weekNumber}")
    public ResponseEntity<List<ActivityRecommendationDto>> getActivityPlanByWeek(
            @PathVariable Long userId, @PathVariable int weekNumber) {
        return ResponseEntity.ok(healthProfileService.generateActivityPlanByUserIdAndWeek(userId, weekNumber));
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

    @GetMapping("/user/{userId}/activities/{weekNumber}/ignore-injuries")
    public ResponseEntity<List<ActivityRecommendationDto>> getActivityPlanIgnoreInjuries(
            @PathVariable Long userId, @PathVariable int weekNumber) {
        return ResponseEntity.ok(healthProfileService.generateActivityPlanByUserIdAndWeek(userId, weekNumber, true));
    }

    // --- AI HEALTH SCORE ENDPOINTS ---
    @PostMapping("/predict")
    @CrossOrigin("*")
    public double predict(@RequestBody AthleteRequest request) {
        return healthProfileService.predictScore(request);
    }

    @PostMapping("/chat")
    @CrossOrigin("*")
    public ChatResponse chat(@RequestBody tn.esprit._4se2.pi.dto.Athlet.ChatRequest request) {
        return healthProfileService.traiteChatMessage(request.getMessage());
    }

    @PostMapping("/user/{userId}/save-score")
    @CrossOrigin("*")
    public void saveScore(@PathVariable Long userId, @RequestBody Map<String, Object> body) {
        Double score = Double.valueOf(body.get("score").toString());
        String assessment = (String) body.get("assessment");
        String prediction = (String) body.get("prediction");
        healthProfileService.saveScore(userId, score, assessment, prediction);
    }

    @GetMapping("/user/{userId}/history")
    @CrossOrigin("*")
    public List<HealthMetrics> getHistory(@PathVariable Long userId) {
        return healthProfileService.getHistory(userId);
    }
}