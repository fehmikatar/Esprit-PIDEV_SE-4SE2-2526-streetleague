package tn.esprit.se2.laakommanel.pi.repositories;

import tn.esprit.se2.laakommanel.pi.entites.DietPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DietPlanRepository extends JpaRepository<DietPlan, Long> {

    // Trouver par profil santé
    List<DietPlan> findByHealthProfileId(Long healthProfileId);

    // Trouver les plans actifs
    List<DietPlan> findByIsActiveTrue();

    // Trouver par nom
    List<DietPlan> findByPlanNameContaining(String planName);

    // Plan actif d'un utilisateur
    @Query("SELECT dp FROM DietPlan dp WHERE " +
            "dp.healthProfile.user.id = :userId AND " +
            "dp.isActive = true")
    Optional<DietPlan> findActiveDietPlanByUserId(@Param("userId") Long userId);

    // Plans par intervalle de calories
    List<DietPlan> findByDailyCaloriesBetween(Integer min, Integer max);

    // Plans qui expirent bientôt
    @Query("SELECT dp FROM DietPlan dp WHERE " +
            "dp.endDate BETWEEN :today AND :nextWeek AND " +
            "dp.isActive = true")
    List<DietPlan> findExpiringPlans(
            @Param("today") LocalDate today,
            @Param("nextWeek") LocalDate nextWeek);

    // Plans créés par quelqu'un
    List<DietPlan> findByCreatedBy(String createdBy);

    // Statistiques des plans actifs
    @Query("SELECT AVG(dp.dailyCalories), MIN(dp.dailyCalories), MAX(dp.dailyCalories) " +
            "FROM DietPlan dp WHERE dp.isActive = true")
    Object[] getCalorieStatistics();

    // Recherche avancée
    @Query("SELECT dp FROM DietPlan dp WHERE " +
            "(:healthProfileId IS NULL OR dp.healthProfile.id = :healthProfileId) AND " +
            "(:isActive IS NULL OR dp.isActive = :isActive) AND " +
            "(:minCalories IS NULL OR dp.dailyCalories >= :minCalories) AND " +
            "(:maxCalories IS NULL OR dp.dailyCalories <= :maxCalories)")
    List<DietPlan> searchDietPlans(
            @Param("healthProfileId") Long healthProfileId,
            @Param("isActive") Boolean isActive,
            @Param("minCalories") Integer minCalories,
            @Param("maxCalories") Integer maxCalories);
}