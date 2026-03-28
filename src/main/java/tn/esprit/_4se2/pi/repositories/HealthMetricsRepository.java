package tn.esprit._4se2.pi.repositories;

import tn.esprit._4se2.pi.entites.HealthMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface HealthMetricsRepository extends JpaRepository<HealthMetrics, Long> {

    List<HealthMetrics> findByHealthProfileIdOrderByMeasuredAtDesc(Long healthProfileId);

    HealthMetrics findFirstByHealthProfileIdOrderByMeasuredAtDesc(Long healthProfileId);

    List<HealthMetrics> findByHealthProfileIdAndMeasuredAtBetween(
            Long healthProfileId, LocalDateTime start, LocalDateTime end);

    List<HealthMetrics> findByMeasuredAt(LocalDateTime date);

    List<HealthMetrics> findByHeartRateBetween(int min, int max);

    List<HealthMetrics> findBySystolicBPGreaterThanEqualAndDiastolicBPGreaterThanEqual(
            int systolic, int diastolic);

    List<HealthMetrics> findByHeartRateLessThanOrHeartRateGreaterThan(int min, int max);

    @Query("SELECT hm.measuredAt, hm.weight FROM HealthMetrics hm " +
            "WHERE hm.healthProfile.id = :healthProfileId ORDER BY hm.measuredAt")
    List<Object[]> getWeightProgression(@Param("healthProfileId") Long healthProfileId);
}