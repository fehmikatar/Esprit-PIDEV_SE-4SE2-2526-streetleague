package tn.esprit.se2.laakommanel.pi.repositories;


import tn.esprit.se2.laakommanel.pi.entites.HealthMetrics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface HealthMetricsRepository extends JpaRepository<HealthMetrics, Long> {

    // Trouver par profil santé (du plus récent au plus ancien)
    List<HealthMetrics> findByHealthProfileIdOrderByMeasuredAtDesc(Long healthProfileId);

    // Dernières métriques d'un profil
    Optional<HealthMetrics> findTopByHealthProfileIdOrderByMeasuredAtDesc(Long healthProfileId);

    // Métriques entre deux dates
    List<HealthMetrics> findByHealthProfileIdAndMeasuredAtBetween(
            Long healthProfileId,
            LocalDateTime start,
            LocalDateTime end);

    // Métriques avec stress élevé
    @Query("SELECT hm FROM HealthMetrics hm WHERE hm.stressLevel >= 7")
    List<HealthMetrics> findHighStressMetrics();

    // Métriques avec faible hydratation
    @Query("SELECT hm FROM HealthMetrics hm WHERE hm.hydration < 50")
    List<HealthMetrics> findLowHydrationMetrics();

    // Moyennes des métriques pour un profil
    @Query("SELECT AVG(hm.weight), AVG(hm.muscleMass), AVG(hm.bodyFat), " +
            "AVG(hm.restingHeartRate), AVG(hm.sleepHours) " +
            "FROM HealthMetrics hm WHERE hm.healthProfile.id = :profileId")
    Object[] getAverageMetrics(@Param("profileId") Long profileId);

    // Évolution du poids
    @Query("SELECT hm.measuredAt, hm.weight FROM HealthMetrics hm " +
            "WHERE hm.healthProfile.id = :profileId ORDER BY hm.measuredAt")
    List<Object[]> getWeightHistory(@Param("profileId") Long profileId);

    // Dernière semaine de métriques
    @Query("SELECT hm FROM HealthMetrics hm WHERE " +
            "hm.healthProfile.id = :profileId AND " +
            "hm.measuredAt >= :oneWeekAgo " +
            "ORDER BY hm.measuredAt DESC")
    List<HealthMetrics> findLastWeekMetrics(
            @Param("profileId") Long profileId,
            @Param("oneWeekAgo") LocalDateTime oneWeekAgo);

    // Alertes santé basées sur les métriques
    @Query("SELECT hm FROM HealthMetrics hm WHERE " +
            "hm.restingHeartRate > 100 OR " +
            "hm.hydration < 40 OR " +
            "hm.stressLevel > 8")
    List<HealthMetrics> findHealthAlerts();
}