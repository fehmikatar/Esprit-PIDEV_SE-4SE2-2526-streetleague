package tn.esprit._4se2.pi.mappers;

import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.dto.HealthMetrics.HealthMetricsRequest;
import tn.esprit._4se2.pi.dto.HealthMetrics.HealthMetricsResponse;
import tn.esprit._4se2.pi.entities.HealthMetrics;

@Component
public class HealthMetricsMapper {

    public HealthMetrics toEntity(HealthMetricsRequest request) {
        if (request == null) return null;

        HealthMetrics metrics = new HealthMetrics();
        metrics.setWeight(request.getWeight());
        metrics.setMuscleMass(request.getMuscleMass());
        metrics.setBodyFat(request.getBodyFat());
        metrics.setHydration(request.getHydration());
        metrics.setRestingHeartRate(request.getRestingHeartRate());
        metrics.setSystolicBP(request.getSystolicBP());
        metrics.setDiastolicBP(request.getDiastolicBP());
        metrics.setSleepHours(request.getSleepHours());
        metrics.setStressLevel(request.getStressLevel());
        metrics.setEnergyLevel(request.getEnergyLevel());
        metrics.setMeasuredAt(request.getMeasuredAt());
        metrics.setNotes(request.getNotes());
        return metrics;
    }

    public HealthMetricsResponse toResponse(HealthMetrics entity) {
        if (entity == null) return null;

        return HealthMetricsResponse.builder()
                .id(entity.getId())
                .healthProfileId(entity.getHealthProfile() != null ? entity.getHealthProfile().getId() : null)
                .weight(entity.getWeight())
                .muscleMass(entity.getMuscleMass())
                .bodyFat(entity.getBodyFat())
                .hydration(entity.getHydration())
                .restingHeartRate(entity.getRestingHeartRate())
                .systolicBP(entity.getSystolicBP())
                .diastolicBP(entity.getDiastolicBP())
                .sleepHours(entity.getSleepHours())
                .stressLevel(entity.getStressLevel())
                .energyLevel(entity.getEnergyLevel())
                .measuredAt(entity.getMeasuredAt())
                .notes(entity.getNotes())
                .build();
    }

    public void updateEntity(HealthMetricsRequest request, HealthMetrics metrics) {
        if (request == null || metrics == null) return;

        metrics.setWeight(request.getWeight());
        metrics.setMuscleMass(request.getMuscleMass());
        metrics.setBodyFat(request.getBodyFat());
        metrics.setHydration(request.getHydration());
        metrics.setRestingHeartRate(request.getRestingHeartRate());
        metrics.setSystolicBP(request.getSystolicBP());
        metrics.setDiastolicBP(request.getDiastolicBP());
        metrics.setSleepHours(request.getSleepHours());
        metrics.setStressLevel(request.getStressLevel());
        metrics.setEnergyLevel(request.getEnergyLevel());
        metrics.setMeasuredAt(request.getMeasuredAt());
        metrics.setNotes(request.getNotes());
    }
}