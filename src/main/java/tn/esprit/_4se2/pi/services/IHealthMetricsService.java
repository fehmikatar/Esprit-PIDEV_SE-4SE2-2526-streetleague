package tn.esprit._4se2.pi.services;

import tn.esprit._4se2.pi.entites.HealthMetrics;
import java.time.LocalDateTime;
import java.util.List;

public interface IHealthMetricsService {
    // CRUD de base
    List<HealthMetrics> getAllHealthMetrics();
    HealthMetrics getHealthMetricsById(Long id);
    HealthMetrics createHealthMetrics(HealthMetrics healthMetrics);
    HealthMetrics updateHealthMetrics(Long id, HealthMetrics healthMetrics);
    void deleteHealthMetrics(Long id);

    // Recherches spécifiques - CORRIGÉ
    List<HealthMetrics> getHealthMetricsByHealthProfile(Long healthProfileId);
    HealthMetrics getLatestHealthMetricsByHealthProfile(Long healthProfileId);
    List<HealthMetrics> getHealthMetricsByDateRange(Long healthProfileId, LocalDateTime startDate, LocalDateTime endDate);
    List<HealthMetrics> getHealthMetricsByDate(LocalDateTime date);
    List<HealthMetrics> getHealthMetricsByHeartRateRange(int min, int max);
    List<HealthMetrics> getHealthMetricsByBloodPressureMin(int minSystolic, int minDiastolic);
    List<HealthMetrics> getAbnormalHeartRateMetrics();
    List<Object[]> getWeightProgression(Long healthProfileId);
}