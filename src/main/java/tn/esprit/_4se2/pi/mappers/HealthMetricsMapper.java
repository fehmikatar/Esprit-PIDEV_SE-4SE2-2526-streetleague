package tn.esprit._4se2.pi.mappers;

import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.dto.HealthMetricsRequest;
import tn.esprit._4se2.pi.dto.HealthMetricsResponse;
import tn.esprit._4se2.pi.entities.HealthMetrics;
import tn.esprit._4se2.pi.entities.HealthProfile;
import tn.esprit._4se2.pi.services.IHealthProfileService;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class HealthMetricsMapper {

    private final IHealthProfileService healthProfileService;

    public HealthMetrics toEntity(HealthMetricsRequest request) {
        if (request == null) return null;

        HealthMetrics metrics = new HealthMetrics();
        metrics.setSystolicBP(request.getSystolicBP());
        metrics.setDiastolicBP(request.getDiastolicBP());
        metrics.setHeartRate(request.getHeartRate());
        metrics.setTemperature(request.getTemperature());
        metrics.setWeight(request.getWeight());
        metrics.setSleepHours(request.getSleepHours());
        metrics.setStepsCount(request.getStepsCount());
        metrics.setNotes(request.getNotes());
        metrics.setMeasuredAt(LocalDateTime.now());

        if (request.getHealthProfileId() != null) {
            HealthProfile profile = new HealthProfile();
            profile.setId(request.getHealthProfileId());
            metrics.setHealthProfile(profile);
        }

        return metrics;
    }

    public HealthMetricsResponse toResponse(HealthMetrics metrics) {
        if (metrics == null) return null;

        return HealthMetricsResponse.builder()
                .id(metrics.getId())
                .healthProfileId(metrics.getHealthProfile() != null ? metrics.getHealthProfile().getId() : null)
                .systolicBP(metrics.getSystolicBP())
                .diastolicBP(metrics.getDiastolicBP())
                .heartRate(metrics.getHeartRate())
                .temperature(metrics.getTemperature())
                .weight(metrics.getWeight())
                .sleepHours(metrics.getSleepHours())
                .stepsCount(metrics.getStepsCount())
                .notes(metrics.getNotes())
                .measuredAt(metrics.getMeasuredAt())
                .build();
    }
}