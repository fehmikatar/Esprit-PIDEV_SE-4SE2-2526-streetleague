package tn.esprit._4se2.pi.mappers;

import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.dto.DietPlan.DietPlanRequest;
import tn.esprit._4se2.pi.dto.DietPlan.DietPlanResponse;
import tn.esprit._4se2.pi.entities.DietPlan;

@Component
public class DietPlanMapper {

    public DietPlan toEntity(DietPlanRequest request) {
        if (request == null) return null;

        DietPlan dietPlan = new DietPlan();
        dietPlan.setPlanName(request.getPlanName());
        dietPlan.setDescription(request.getDescription());
        dietPlan.setDailyCalories(request.getDailyCalories());
        dietPlan.setMealSuggestions(request.getMealSuggestions());
        dietPlan.setStartDate(request.getStartDate());
        dietPlan.setEndDate(request.getEndDate());
        dietPlan.setIsActive(request.getIsActive());
        dietPlan.setDietaryRestrictions(request.getDietaryRestrictions());
        dietPlan.setNutritionalGoals(request.getNutritionalGoals());
        dietPlan.setCreatedBy(request.getCreatedBy());
        return dietPlan;
    }

    public DietPlanResponse toResponse(DietPlan entity) {
        if (entity == null) return null;

        return DietPlanResponse.builder()
                .id(entity.getId())
                .healthProfileId(entity.getHealthProfile() != null ? entity.getHealthProfile().getId() : null)
                .planName(entity.getPlanName())
                .description(entity.getDescription())
                .dailyCalories(entity.getDailyCalories())
                .mealSuggestions(entity.getMealSuggestions())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .isActive(entity.getIsActive())
                .dietaryRestrictions(entity.getDietaryRestrictions())
                .nutritionalGoals(entity.getNutritionalGoals())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public void updateEntity(DietPlanRequest request, DietPlan dietPlan) {
        if (request == null || dietPlan == null) return;

        dietPlan.setPlanName(request.getPlanName());
        dietPlan.setDescription(request.getDescription());
        dietPlan.setDailyCalories(request.getDailyCalories());
        dietPlan.setMealSuggestions(request.getMealSuggestions());
        dietPlan.setStartDate(request.getStartDate());
        dietPlan.setEndDate(request.getEndDate());
        dietPlan.setIsActive(request.getIsActive());
        dietPlan.setDietaryRestrictions(request.getDietaryRestrictions());
        dietPlan.setNutritionalGoals(request.getNutritionalGoals());
        dietPlan.setCreatedBy(request.getCreatedBy());
    }
}