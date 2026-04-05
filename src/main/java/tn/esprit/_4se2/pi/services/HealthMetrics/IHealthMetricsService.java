package tn.esprit._4se2.pi.services.HealthMetrics;

import tn.esprit._4se2.pi.dto.HealthMetrics.HealthMetricsRequest;
import tn.esprit._4se2.pi.dto.HealthMetrics.HealthMetricsResponse;
import java.time.LocalDateTime;
import java.util.List;

public interface IHealthMetricsService {
    HealthMetricsResponse createHealthMetrics(HealthMetricsRequest request);
    HealthMetricsResponse getHealthMetricsById(Long id);
    List<HealthMetricsResponse> getAllHealthMetrics();
    List<HealthMetricsResponse> getHealthMetricsByHealthProfileId(Long healthProfileId);
    List<HealthMetricsResponse> getHealthMetricsByDateRange(Long healthProfileId, LocalDateTime start, LocalDateTime end);
    HealthMetricsResponse updateHealthMetrics(Long id, HealthMetricsRequest request);
    void deleteHealthMetrics(Long id);
}