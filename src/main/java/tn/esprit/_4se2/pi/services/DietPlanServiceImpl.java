package tn.esprit._4se2.pi.services;

import tn.esprit._4se2.pi.entites.DietPlan;
import tn.esprit._4se2.pi.repositories.DietPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DietPlanServiceImpl implements IDietPlanService {

    private final DietPlanRepository dietPlanRepository;

    // =============== CRUD de base ===============

    @Override
    public List<DietPlan> getAllDietPlans() {
        return dietPlanRepository.findAll();
    }

    @Override
    public DietPlan getDietPlanById(Long id) {
        return dietPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DietPlan not found with id: " + id));
    }

    @Override
    public DietPlan createDietPlan(DietPlan dietPlan) {
        // Validation
        if (dietPlan.getPlanName() == null || dietPlan.getPlanName().trim().isEmpty()) {
            throw new RuntimeException("Plan name is required");
        }
        if (dietPlan.getHealthProfile() == null) {
            throw new RuntimeException("Health profile is required");
        }
        if (dietPlan.getStartDate() != null && dietPlan.getEndDate() != null
                && dietPlan.getStartDate().isAfter(dietPlan.getEndDate())) {
            throw new RuntimeException("Start date must be before end date");
        }

        return dietPlanRepository.save(dietPlan);
    }

    @Override
    public DietPlan updateDietPlan(Long id, DietPlan planDetails) {
        DietPlan existingPlan = getDietPlanById(id);

        // Mise à jour des champs
        if (planDetails.getPlanName() != null) {
            existingPlan.setPlanName(planDetails.getPlanName());
        }
        if (planDetails.getDescription() != null) {
            existingPlan.setDescription(planDetails.getDescription());
        }
        if (planDetails.getDailyCalories() != null) {
            existingPlan.setDailyCalories(planDetails.getDailyCalories());
        }
        if (planDetails.getStartDate() != null) {
            existingPlan.setStartDate(planDetails.getStartDate());
        }
        if (planDetails.getEndDate() != null) {
            existingPlan.setEndDate(planDetails.getEndDate());
        }
        if (planDetails.getIsActive() != null) {
            existingPlan.setIsActive(planDetails.getIsActive());
        }
        if (planDetails.getMealSuggestions() != null) {
            existingPlan.setMealSuggestions(planDetails.getMealSuggestions());
        }
        if (planDetails.getDietaryRestrictions() != null) {
            existingPlan.setDietaryRestrictions(planDetails.getDietaryRestrictions());
        }
        if (planDetails.getNutritionalGoals() != null) {
            existingPlan.setNutritionalGoals(planDetails.getNutritionalGoals());
        }

        return dietPlanRepository.save(existingPlan);
    }

    @Override
    public void deleteDietPlan(Long id) {
        DietPlan dietPlan = getDietPlanById(id);
        dietPlanRepository.delete(dietPlan);
    }

    // =============== Recherches spécifiques ===============

    @Override
    public List<DietPlan> getDietPlansByHealthProfile(Long healthProfileId) {
        if (healthProfileId == null) {
            throw new RuntimeException("Health profile ID cannot be null");
        }
        return dietPlanRepository.findByHealthProfileId(healthProfileId);
    }

    @Override
    public List<DietPlan> getDietPlansByMaxDailyCalories(int maxCalories) {
        return dietPlanRepository.findByDailyCaloriesLessThanEqual(maxCalories);
    }

    @Override
    public List<DietPlan> getDietPlansByDailyCaloriesRange(int minCalories, int maxCalories) {
        if (minCalories > maxCalories) {
            throw new RuntimeException("Min calories must be less than max calories");
        }
        return dietPlanRepository.findByDailyCaloriesBetween(minCalories, maxCalories);
    }

    @Override
    public List<DietPlan> getActivePlansByHealthProfile(Long healthProfileId) {
        if (healthProfileId == null) {
            throw new RuntimeException("Health profile ID cannot be null");
        }
        return dietPlanRepository.findActivePlansByHealthProfile(healthProfileId);
    }

    @Override
    public List<DietPlan> getPlansEndingSoon(LocalDate futureDate) {
        if (futureDate == null) {
            futureDate = LocalDate.now().plusDays(7); // Par défaut: 7 jours
        }
        return dietPlanRepository.findPlansEndingSoon(futureDate);
    }

    @Override
    public List<DietPlan> searchByPlanName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Search name cannot be empty");
        }
        return dietPlanRepository.findByPlanNameContainingIgnoreCase(name);
    }

    // =============== Statistiques ===============

    @Override
    public Double getAverageDailyCaloriesByHealthProfile(Long healthProfileId) {
        if (healthProfileId == null) {
            throw new RuntimeException("Health profile ID cannot be null");
        }
        return dietPlanRepository.getAverageDailyCaloriesByHealthProfile(healthProfileId);
    }

    @Override
    public List<Object[]> getMostPopularDietPlans() {
        return dietPlanRepository.findMostPopularDietPlans();
    }

    // =============== Méthodes utilitaires supplémentaires ===============

    /**
     * Activer/Désactiver un plan
     */
    public DietPlan togglePlanStatus(Long id) {
        DietPlan plan = getDietPlanById(id);
        plan.setIsActive(!plan.getIsActive());
        return dietPlanRepository.save(plan);
    }

    /**
     * Obtenir les plans actifs pour aujourd'hui
     */
    public List<DietPlan> getTodayActivePlans() {
        LocalDate today = LocalDate.now();
        return dietPlanRepository.findAll().stream()
                .filter(p -> p.getIsActive() != null && p.getIsActive())
                .filter(p -> p.getStartDate() != null && !p.getStartDate().isAfter(today))
                .filter(p -> p.getEndDate() == null || !p.getEndDate().isBefore(today))
                .toList();
    }
}