package tn.esprit._4se2.pi.dto.DietPlan;

import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DietPlanRequest {

    @NotNull(message = "Health profile ID is required")
    Long healthProfileId;

    @NotBlank(message = "Plan name is required")
    @Size(min = 3, max = 100)
    String planName;

    @Size(max = 500)
    String description;

    @Min(value = 500, message = "Daily calories must be at least 500")
    @Max(value = 5000, message = "Daily calories cannot exceed 5000")
    Integer dailyCalories;

    @Size(max = 2000)
    String mealSuggestions;

    @NotNull(message = "Start date is required")
    LocalDate startDate;

    LocalDate endDate;

    Boolean isActive;

    @Size(max = 255)
    String dietaryRestrictions;

    @Size(max = 255)
    String nutritionalGoals;

    @NotBlank(message = "Created by is required")
    @Size(max = 100)
    String createdBy;
}