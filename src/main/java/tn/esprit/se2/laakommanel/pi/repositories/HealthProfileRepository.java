package tn.esprit.se2.laakommanel.pi.repositories;

import tn.esprit.se2.laakommanel.pi.entites.HealthProfile;
import tn.esprit.se2.laakommanel.pi.entites.FitnessStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface HealthProfileRepository extends JpaRepository<HealthProfile, Long> {

    // Trouver par utilisateur
    Optional<HealthProfile> findByUserId(Long userId);

    // Trouver par statut de forme
    List<HealthProfile> findByFitnessStatus(FitnessStatus status);

    // Compter par statut de forme
    Long countByFitnessStatus(FitnessStatus status);

    // Vérifier si un profil existe pour un utilisateur
    boolean existsByUserId(Long userId);

    // Trouver les profils avec IMC > certain seuil
    @Query("SELECT hp FROM HealthProfile hp WHERE (hp.weight / ((hp.height/100) * (hp.height/100))) > :bmi")
    List<HealthProfile> findByBmiGreaterThan(@Param("bmi") Double bmi);

    // Trouver par tranche d'âge
    List<HealthProfile> findByAgeBetween(Integer minAge, Integer maxAge);

    // Top 10 IMC
    @Query(value = "SELECT * FROM health_profiles ORDER BY (weight / POW(height/100, 2)) DESC LIMIT 10",
            nativeQuery = true)
    List<HealthProfile> findTop10ByBMI();

    // Statistiques par statut
    @Query("SELECT hp.fitnessStatus, COUNT(hp) FROM HealthProfile hp GROUP BY hp.fitnessStatus")
    List<Object[]> getFitnessStatusStatistics();

    // Profils avec alertes santé
    @Query("SELECT hp FROM HealthProfile hp WHERE " +
            "hp.fitnessStatus = 'INJURED' OR " +
            "hp.fitnessStatus = 'RECOVERING'")
    List<HealthProfile> findProfilesWithHealthAlerts();

    // Recherche multicritères
    @Query("SELECT hp FROM HealthProfile hp WHERE " +
            "(:minWeight IS NULL OR hp.weight >= :minWeight) AND " +
            "(:maxWeight IS NULL OR hp.weight <= :maxWeight) AND " +
            "(:minHeight IS NULL OR hp.height >= :minHeight) AND " +
            "(:maxHeight IS NULL OR hp.height <= :maxHeight) AND " +
            "(:status IS NULL OR hp.fitnessStatus = :status)")
    List<HealthProfile> searchHealthProfiles(
            @Param("minWeight") Double minWeight,
            @Param("maxWeight") Double maxWeight,
            @Param("minHeight") Double minHeight,
            @Param("maxHeight") Double maxHeight,
            @Param("status") FitnessStatus status);
}