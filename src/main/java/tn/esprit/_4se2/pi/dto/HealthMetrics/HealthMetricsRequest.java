package tn.esprit._4se2.pi.dto.HealthMetrics;

import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HealthMetricsRequest {

    @NotNull(message = "Health profile ID is required")
    Long healthProfileId;

    @Positive(message = "Weight must be positive")
    Double weight;

    @DecimalMin(value = "0.0", message = "Muscle mass must be >= 0")
    @DecimalMax(value = "100.0", message = "Muscle mass must be <= 100")
    Double muscleMass;

    @DecimalMin(value = "0.0", message = "Body fat must be >= 0")
    @DecimalMax(value = "100.0", message = "Body fat must be <= 100")
    Double bodyFat;

    @DecimalMin(value = "0.0", message = "Hydration must be >= 0")
    @DecimalMax(value = "100.0", message = "Hydration must be <= 100")
    Double hydration;

    @Min(value = 30, message = "Resting heart rate must be at least 30")
    @Max(value = 200, message = "Resting heart rate must not exceed 200")
    Integer restingHeartRate;

    @Min(value = 70, message = "Systolic BP must be at least 70")
    @Max(value = 250, message = "Systolic BP must not exceed 250")
    Integer systolicBP;

    @Min(value = 40, message = "Diastolic BP must be at least 40")
    @Max(value = 150, message = "Diastolic BP must not exceed 150")
    Integer diastolicBP;

    @Min(value = 0, message = "Sleep hours must be >= 0")
    @Max(value = 24, message = "Sleep hours must be <= 24")
    Integer sleepHours;

    @Min(value = 1, message = "Stress level must be between 1 and 10")
    @Max(value = 10, message = "Stress level must be between 1 and 10")
    Integer stressLevel;

    @Min(value = 1, message = "Energy level must be between 1 and 10")
    @Max(value = 10, message = "Energy level must be between 1 and 10")
    Integer energyLevel;

    LocalDateTime measuredAt; // optional, defaults to now

    @Size(max = 500)
    String notes;
}