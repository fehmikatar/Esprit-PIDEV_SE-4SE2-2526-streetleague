package tn.esprit._4se2.pi.dto.DietPlan;

import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DietPlanResponse {
    private Long id;
    private Long healthProfileId;
    private String planName;
    private String description;
    private Integer dailyCalories;
    private String mealSuggestions;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
    private String dietaryRestrictions;
    private String nutritionalGoals;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}