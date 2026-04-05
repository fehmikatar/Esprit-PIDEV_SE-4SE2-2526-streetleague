package tn.esprit._4se2.pi.restcontrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.dto.HealthMetrics.HealthMetricsRequest;
import tn.esprit._4se2.pi.dto.HealthMetrics.HealthMetricsResponse;
import tn.esprit._4se2.pi.services.HealthMetrics.IHealthMetricsService;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/health-metrics")
@RequiredArgsConstructor
public class HealthMetricsRestController {

    private final IHealthMetricsService healthMetricsService;

    @PostMapping
    public ResponseEntity<HealthMetricsResponse> createHealthMetrics(@Valid @RequestBody HealthMetricsRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(healthMetricsService.createHealthMetrics(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<HealthMetricsResponse> getHealthMetricsById(@PathVariable Long id) {
        return ResponseEntity.ok(healthMetricsService.getHealthMetricsById(id));
    }

    @GetMapping
    public ResponseEntity<List<HealthMetricsResponse>> getAllHealthMetrics() {
        return ResponseEntity.ok(healthMetricsService.getAllHealthMetrics());
    }

    @GetMapping("/health-profile/{healthProfileId}")
    public ResponseEntity<List<HealthMetricsResponse>> getHealthMetricsByHealthProfileId(@PathVariable Long healthProfileId) {
        return ResponseEntity.ok(healthMetricsService.getHealthMetricsByHealthProfileId(healthProfileId));
    }

    @GetMapping("/health-profile/{healthProfileId}/date-range")
    public ResponseEntity<List<HealthMetricsResponse>> getHealthMetricsByDateRange(
            @PathVariable Long healthProfileId,
            @RequestParam LocalDateTime start,
            @RequestParam LocalDateTime end) {
        return ResponseEntity.ok(healthMetricsService.getHealthMetricsByDateRange(healthProfileId, start, end));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HealthMetricsResponse> updateHealthMetrics(
            @PathVariable Long id,
            @Valid @RequestBody HealthMetricsRequest request) {
        return ResponseEntity.ok(healthMetricsService.updateHealthMetrics(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHealthMetrics(@PathVariable Long id) {
        healthMetricsService.deleteHealthMetrics(id);
        return ResponseEntity.noContent().build();
    }
}