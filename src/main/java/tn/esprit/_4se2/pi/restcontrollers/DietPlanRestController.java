package tn.esprit._4se2.pi.restcontrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.dto.DietPlan.DietPlanRequest;
import tn.esprit._4se2.pi.dto.DietPlan.DietPlanResponse;
import tn.esprit._4se2.pi.services.DietPlan.IDietPlanService;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/diet-plans")
@RequiredArgsConstructor
public class DietPlanRestController {

    private final IDietPlanService dietPlanService;

    @PostMapping
    public ResponseEntity<DietPlanResponse> createDietPlan(@Valid @RequestBody DietPlanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(dietPlanService.createDietPlan(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DietPlanResponse> getDietPlanById(@PathVariable Long id) {
        return ResponseEntity.ok(dietPlanService.getDietPlanById(id));
    }

    @GetMapping
    public ResponseEntity<List<DietPlanResponse>> getAllDietPlans() {
        return ResponseEntity.ok(dietPlanService.getAllDietPlans());
    }

    @GetMapping("/health-profile/{healthProfileId}")
    public ResponseEntity<List<DietPlanResponse>> getDietPlansByHealthProfileId(@PathVariable Long healthProfileId) {
        return ResponseEntity.ok(dietPlanService.getDietPlansByHealthProfileId(healthProfileId));
    }

    @GetMapping("/active")
    public ResponseEntity<List<DietPlanResponse>> getActiveDietPlans() {
        return ResponseEntity.ok(dietPlanService.getActiveDietPlans());
    }

    @GetMapping("/health-profile/{healthProfileId}/active")
    public ResponseEntity<List<DietPlanResponse>> getActiveDietPlansByHealthProfile(@PathVariable Long healthProfileId) {
        return ResponseEntity.ok(dietPlanService.getActiveDietPlansByHealthProfile(healthProfileId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DietPlanResponse> updateDietPlan(
            @PathVariable Long id,
            @Valid @RequestBody DietPlanRequest request) {
        return ResponseEntity.ok(dietPlanService.updateDietPlan(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDietPlan(@PathVariable Long id) {
        dietPlanService.deleteDietPlan(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateDietPlan(@PathVariable Long id) {
        dietPlanService.activateDietPlan(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateDietPlan(@PathVariable Long id) {
        dietPlanService.deactivateDietPlan(id);
        return ResponseEntity.noContent().build();
    }
}