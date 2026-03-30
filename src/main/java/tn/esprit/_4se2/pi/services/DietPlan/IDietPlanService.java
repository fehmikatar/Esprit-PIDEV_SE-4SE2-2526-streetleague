package tn.esprit._4se2.pi.services.DietPlan;

import tn.esprit._4se2.pi.entities.DietPlan;

import java.time.LocalDate;
import java.util.List;

public interface IDietPlanService {
    // CRUD de base
    List<DietPlan> getAllDietPlans();
    DietPlan getDietPlanById(Long id);
    DietPlan createDietPlan(DietPlan dietPlan);
    DietPlan updateDietPlan(Long id, DietPlan dietPlan);
    void deleteDietPlan(Long id);

    // Recherches spécifiques - CORRIGÉ
    List<DietPlan> getDietPlansByHealthProfile(Long healthProfileId);  // au lieu de getDietPlansByPatient
    List<DietPlan> getDietPlansByMaxDailyCalories(int maxCalories);    // au lieu de getDietPlansByMaxCalories
    List<DietPlan> getDietPlansByDailyCaloriesRange(int minCalories, int maxCalories);
    List<DietPlan> getActivePlansByHealthProfile(Long healthProfileId);
    List<DietPlan> getPlansEndingSoon(LocalDate futureDate);
    List<DietPlan> searchByPlanName(String name);

    // Statistiques
    Double getAverageDailyCaloriesByHealthProfile(Long healthProfileId);
    List<Object[]> getMostPopularDietPlans();

    List<DietPlan> getTodayActivePlans();

    DietPlan togglePlanStatus(Long id);
}