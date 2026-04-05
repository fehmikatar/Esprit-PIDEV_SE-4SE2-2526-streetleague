package tn.esprit._4se2.pi.dto.DietPlan;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DietPlanResponse {
    Long id;
    Long healthProfileId;
    String planName;
    String description;
    Integer dailyCalories;
    String mealSuggestions;
    LocalDate startDate;
    LocalDate endDate;
    Boolean isActive;
    String dietaryRestrictions;
    String nutritionalGoals;
    String createdBy;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}