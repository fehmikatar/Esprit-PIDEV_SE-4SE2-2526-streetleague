package tn.esprit._4se2.pi.mappers;

import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.dto.DietPlanRequest;
import tn.esprit._4se2.pi.dto.DietPlanResponse;
import tn.esprit._4se2.pi.entities.DietPlan;
import tn.esprit._4se2.pi.entities.HealthProfile;
import tn.esprit._4se2.pi.services.IHealthProfileService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DietPlanMapper {

    private final IHealthProfileService healthProfileService;

    public DietPlan toEntity(DietPlanRequest request) {
        if (request == null) return null;

        DietPlan plan = new DietPlan();
        plan.setPlanName(request.getPlanName());
        plan.setDescription(request.getDescription());
        plan.setDailyCalories(request.getDailyCalories());
        plan.setMealSuggestions(request.getMealSuggestions());
        plan.setStartDate(request.getStartDate());
        plan.setEndDate(request.getEndDate());
        plan.setIsActive(request.getIsActive());
        plan.setDietaryRestrictions(request.getDietaryRestrictions());
        plan.setNutritionalGoals(request.getNutritionalGoals());
        plan.setCreatedBy(request.getCreatedBy());

        if (request.getHealthProfileId() != null) {
            HealthProfile healthProfile = new HealthProfile();
            healthProfile.setId(request.getHealthProfileId());
            plan.setHealthProfile(healthProfile);
        }

        return plan;
    }

    public DietPlanResponse toResponse(DietPlan plan) {
        if (plan == null) return null;

        return DietPlanResponse.builder()
                .id(plan.getId())
                .healthProfileId(plan.getHealthProfile() != null ? plan.getHealthProfile().getId() : null)
                .planName(plan.getPlanName())
                .description(plan.getDescription())
                .dailyCalories(plan.getDailyCalories())
                .mealSuggestions(plan.getMealSuggestions())
                .startDate(plan.getStartDate())
                .endDate(plan.getEndDate())
                .isActive(plan.getIsActive())
                .dietaryRestrictions(plan.getDietaryRestrictions())
                .nutritionalGoals(plan.getNutritionalGoals())
                .createdBy(plan.getCreatedBy())
                .createdAt(plan.getCreatedAt())
                .updatedAt(plan.getUpdatedAt())
                .build();
    }
}