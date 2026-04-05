package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.entities.HealthMetrics;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HealthMetricsRepository extends JpaRepository<HealthMetrics, Long> {
    List<HealthMetrics> findByHealthProfileId(Long healthProfileId);
    List<HealthMetrics> findByHealthProfileIdAndMeasuredAtBetween(Long healthProfileId, LocalDateTime start, LocalDateTime end);
    List<HealthMetrics> findByMeasuredAtBetween(LocalDateTime start, LocalDateTime end);
}