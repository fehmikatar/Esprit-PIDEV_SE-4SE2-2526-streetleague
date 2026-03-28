package tn.esprit._4se2.pi.services;

import tn.esprit._4se2.pi.entites.HealthMetrics;
import tn.esprit._4se2.pi.repositories.HealthMetricsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class HealthMetricsServiceImpl implements IHealthMetricsService {

    private final HealthMetricsRepository healthMetricsRepository;

    // =============== CRUD de base ===============

    @Override
    public List<HealthMetrics> getAllHealthMetrics() {
        return healthMetricsRepository.findAll();
    }

    @Override
    public HealthMetrics getHealthMetricsById(Long id) {
        return healthMetricsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HealthMetrics not found with id: " + id));
    }

    @Override
    public HealthMetrics createHealthMetrics(HealthMetrics healthMetrics) {
        // Validation
        if (healthMetrics.getHealthProfile() == null) {
            throw new RuntimeException("Health profile is required");
        }
        if (healthMetrics.getMeasuredAt() == null) {
            healthMetrics.setMeasuredAt(LocalDateTime.now());
        }

        return healthMetricsRepository.save(healthMetrics);
    }

    @Override
    public HealthMetrics updateHealthMetrics(Long id, HealthMetrics metricsDetails) {
        HealthMetrics existingMetrics = getHealthMetricsById(id);

        // ✅ Mise à jour uniquement des champs qui existent dans l'entité
        if (metricsDetails.getWeight() != null) {
            existingMetrics.setWeight(metricsDetails.getWeight());
        }
        if (metricsDetails.getSystolicBP() != null) {
            existingMetrics.setSystolicBP(metricsDetails.getSystolicBP());
        }
        if (metricsDetails.getDiastolicBP() != null) {
            existingMetrics.setDiastolicBP(metricsDetails.getDiastolicBP());
        }
        if (metricsDetails.getHeartRate() != null) {
            existingMetrics.setHeartRate(metricsDetails.getHeartRate());
        }
        if (metricsDetails.getTemperature() != null) {
            existingMetrics.setTemperature(metricsDetails.getTemperature());
        }
        if (metricsDetails.getSleepHours() != null) {
            existingMetrics.setSleepHours(metricsDetails.getSleepHours());
        }
        if (metricsDetails.getStepsCount() != null) {
            existingMetrics.setStepsCount(metricsDetails.getStepsCount());
        }
        if (metricsDetails.getNotes() != null) {
            existingMetrics.setNotes(metricsDetails.getNotes());
        }

        return healthMetricsRepository.save(existingMetrics);
    }

    @Override
    public void deleteHealthMetrics(Long id) {
        HealthMetrics metrics = getHealthMetricsById(id);
        healthMetricsRepository.delete(metrics);
    }

    // =============== Recherches spécifiques ===============

    @Override
    public List<HealthMetrics> getHealthMetricsByHealthProfile(Long healthProfileId) {
        if (healthProfileId == null) {
            throw new RuntimeException("Health profile ID cannot be null");
        }
        return healthMetricsRepository.findByHealthProfileIdOrderByMeasuredAtDesc(healthProfileId);
    }

    @Override
    public HealthMetrics getLatestHealthMetricsByHealthProfile(Long healthProfileId) {
        if (healthProfileId == null) {
            throw new RuntimeException("Health profile ID cannot be null");
        }
        return healthMetricsRepository.findFirstByHealthProfileIdOrderByMeasuredAtDesc(healthProfileId);
    }

    @Override
    public List<HealthMetrics> getHealthMetricsByDateRange(Long healthProfileId, LocalDateTime startDate, LocalDateTime endDate) {
        if (healthProfileId == null) {
            throw new RuntimeException("Health profile ID cannot be null");
        }
        if (startDate == null || endDate == null) {
            throw new RuntimeException("Start and end dates cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new RuntimeException("Start date must be before end date");
        }
        return healthMetricsRepository.findByHealthProfileIdAndMeasuredAtBetween(healthProfileId, startDate, endDate);
    }

    @Override
    public List<HealthMetrics> getHealthMetricsByDate(LocalDateTime date) {
        if (date == null) {
            throw new RuntimeException("Date cannot be null");
        }
        return healthMetricsRepository.findByMeasuredAt(date);
    }

    @Override
    public List<HealthMetrics> getHealthMetricsByHeartRateRange(int min, int max) {
        if (min > max) {
            throw new RuntimeException("Min heart rate must be less than max heart rate");
        }
        // ✅ Utilisation de heartRate (qui existe) au lieu de restingHeartRate
        return healthMetricsRepository.findByHeartRateBetween(min, max);
    }

    @Override
    public List<HealthMetrics> getHealthMetricsByBloodPressureMin(int minSystolic, int minDiastolic) {
        // ✅ Implémentation avec les champs existants
        return healthMetricsRepository.findBySystolicBPGreaterThanEqualAndDiastolicBPGreaterThanEqual(
                minSystolic, minDiastolic);
    }

    @Override
    public List<HealthMetrics> getAbnormalHeartRateMetrics() {
        // ✅ Définition: rythme cardiaque anormal (<60 ou >100)
        return healthMetricsRepository.findByHeartRateLessThanOrHeartRateGreaterThan(60, 100);
    }

    @Override
    public List<Object[]> getWeightProgression(Long healthProfileId) {
        if (healthProfileId == null) {
            throw new RuntimeException("Health profile ID cannot be null");
        }
        return healthMetricsRepository.getWeightProgression(healthProfileId);
    }
}