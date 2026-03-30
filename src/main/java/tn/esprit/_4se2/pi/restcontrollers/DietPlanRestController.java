package tn.esprit._4se2.pi.restcontrollers;

import io.swagger.v3.oas.annotations.tags.Tag;
import tn.esprit._4se2.pi.entities.DietPlan;
import tn.esprit._4se2.pi.dto.DietPlan.DietPlanRequest;
import tn.esprit._4se2.pi.dto.DietPlan.DietPlanResponse;
import tn.esprit._4se2.pi.services.DietPlan.IDietPlanService;
import tn.esprit._4se2.pi.mappers.DietPlanMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/diet-plans")
@RequiredArgsConstructor
@Tag(name = "Diet Plans", description = "🥗 Nutrition and diet plans")
public class DietPlanRestController {

    private final IDietPlanService dietPlanService;
    private final DietPlanMapper dietPlanMapper;

    @GetMapping
    public ResponseEntity<List<DietPlanResponse>> getAllDietPlans() {
        List<DietPlan> plans = dietPlanService.getAllDietPlans();
        List<DietPlanResponse> responses = plans.stream()
                .map(dietPlanMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DietPlanResponse> getDietPlanById(@PathVariable Long id) {
        DietPlan plan = dietPlanService.getDietPlanById(id);
        DietPlanResponse response = dietPlanMapper.toResponse(plan);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<DietPlanResponse> createDietPlan(@RequestBody DietPlanRequest request) {
        DietPlan plan = dietPlanMapper.toEntity(request);
        DietPlan created = dietPlanService.createDietPlan(plan);
        DietPlanResponse response = dietPlanMapper.toResponse(created);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DietPlanResponse> updateDietPlan(@PathVariable Long id, @RequestBody DietPlanRequest request) {
        DietPlan plan = dietPlanMapper.toEntity(request);
        DietPlan updated = dietPlanService.updateDietPlan(id, plan);
        DietPlanResponse response = dietPlanMapper.toResponse(updated);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDietPlan(@PathVariable Long id) {
        dietPlanService.deleteDietPlan(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/health-profile/{healthProfileId}")
    public ResponseEntity<List<DietPlanResponse>> getDietPlansByHealthProfile(@PathVariable Long healthProfileId) {
        List<DietPlan> plans = dietPlanService.getDietPlansByHealthProfile(healthProfileId);
        List<DietPlanResponse> responses = plans.stream()
                .map(dietPlanMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/daily-calories/max/{maxCalories}")
    public ResponseEntity<List<DietPlanResponse>> getByMaxDailyCalories(@PathVariable int maxCalories) {
        List<DietPlan> plans = dietPlanService.getDietPlansByMaxDailyCalories(maxCalories);
        List<DietPlanResponse> responses = plans.stream()
                .map(dietPlanMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/daily-calories/range")
    public ResponseEntity<List<DietPlanResponse>> getByDailyCaloriesRange(
            @RequestParam int min,
            @RequestParam int max) {
        List<DietPlan> plans = dietPlanService.getDietPlansByDailyCaloriesRange(min, max);
        List<DietPlanResponse> responses = plans.stream()
                .map(dietPlanMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/health-profile/{healthProfileId}/active")
    public ResponseEntity<List<DietPlanResponse>> getActivePlansByHealthProfile(@PathVariable Long healthProfileId) {
        List<DietPlan> plans = dietPlanService.getActivePlansByHealthProfile(healthProfileId);
        List<DietPlanResponse> responses = plans.stream()
                .map(dietPlanMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/ending-soon")
    public ResponseEntity<List<DietPlanResponse>> getPlansEndingSoon(
            @RequestParam(required = false) LocalDate futureDate) {
        List<DietPlan> plans = dietPlanService.getPlansEndingSoon(futureDate);
        List<DietPlanResponse> responses = plans.stream()
                .map(dietPlanMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search")
    public ResponseEntity<List<DietPlanResponse>> searchByPlanName(@RequestParam String name) {
        List<DietPlan> plans = dietPlanService.searchByPlanName(name);
        List<DietPlanResponse> responses = plans.stream()
                .map(dietPlanMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/health-profile/{healthProfileId}/average-calories")
    public ResponseEntity<Double> getAverageDailyCalories(@PathVariable Long healthProfileId) {
        Double average = dietPlanService.getAverageDailyCaloriesByHealthProfile(healthProfileId);
        return ResponseEntity.ok(average);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<Object[]>> getMostPopularDietPlans() {
        List<Object[]> popular = dietPlanService.getMostPopularDietPlans();
        return ResponseEntity.ok(popular);
    }

    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<DietPlanResponse> togglePlanStatus(@PathVariable Long id) {
        DietPlan plan = dietPlanService.togglePlanStatus(id);
        DietPlanResponse response = dietPlanMapper.toResponse(plan);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/today-active")
    public ResponseEntity<List<DietPlanResponse>> getTodayActivePlans() {
        List<DietPlan> plans = dietPlanService.getTodayActivePlans();
        List<DietPlanResponse> responses = plans.stream()
                .map(dietPlanMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}