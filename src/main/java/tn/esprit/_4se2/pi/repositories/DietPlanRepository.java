package tn.esprit._4se2.pi.repositories;

import tn.esprit._4se2.pi.entites.DietPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface DietPlanRepository extends JpaRepository<DietPlan, Long> {

    // ✅ CORRIGÉ: Find by healthProfile (pas patient)
    @Query("SELECT dp FROM DietPlan dp WHERE dp.healthProfile.id = :healthProfileId")
    List<DietPlan> findByHealthProfileId(@Param("healthProfileId") Long healthProfileId);

    // ✅ CORRIGÉ: Find by dailyCalories less than or equal
    List<DietPlan> findByDailyCaloriesLessThanEqual(int maxCalories);

    // ✅ CORRIGÉ: Find by dailyCalories between
    List<DietPlan> findByDailyCaloriesBetween(int minCalories, int maxCalories);

    // ✅ CORRIGÉ: Find active plans for a health profile
    @Query("SELECT dp FROM DietPlan dp WHERE dp.healthProfile.id = :healthProfileId AND dp.startDate <= CURRENT_DATE AND dp.endDate >= CURRENT_DATE")
    List<DietPlan> findActivePlansByHealthProfile(@Param("healthProfileId") Long healthProfileId);

    // ✅ OK: plans ending soon (inchangé)
    @Query("SELECT dp FROM DietPlan dp WHERE dp.endDate BETWEEN CURRENT_DATE AND :futureDate")
    List<DietPlan> findPlansEndingSoon(@Param("futureDate") LocalDate futureDate);

    // ✅ OK: find by name containing
    List<DietPlan> findByPlanNameContainingIgnoreCase(String name);

    // ✅ CORRIGÉ: Find by health profile and date range
    @Query("SELECT dp FROM DietPlan dp WHERE dp.healthProfile.id = :healthProfileId AND dp.startDate BETWEEN :start AND :end")
    List<DietPlan> findByHealthProfileIdAndStartDateBetween(@Param("healthProfileId") Long healthProfileId,
                                                            @Param("start") LocalDate start,
                                                            @Param("end") LocalDate end);

    // ✅ CORRIGÉ: Calculate average dailyCalories by health profile
    @Query("SELECT AVG(dp.dailyCalories) FROM DietPlan dp WHERE dp.healthProfile.id = :healthProfileId")
    Double getAverageDailyCaloriesByHealthProfile(@Param("healthProfileId") Long healthProfileId);

    // ✅ OK: find most popular diet plans (inchangé)
    @Query("SELECT dp.planName, COUNT(dp) FROM DietPlan dp GROUP BY dp.planName ORDER BY COUNT(dp) DESC")
    List<Object[]> findMostPopularDietPlans();
}