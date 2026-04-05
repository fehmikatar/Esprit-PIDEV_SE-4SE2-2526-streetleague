package tn.esprit._4se2.pi.dto.HealthProfile;

import lombok.*;
import lombok.experimental.FieldDefaults;
import tn.esprit._4se2.pi.Enum.FitnessStatus;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HealthProfileRequest {

    @NotNull(message = "User ID is required")
    Long userId;

    @Positive(message = "Weight must be positive")
    Double weight;

    @Positive(message = "Height must be positive")
    @DecimalMin(value = "50.0", message = "Height must be at least 50 cm")
    @DecimalMax(value = "300.0", message = "Height cannot exceed 300 cm")
    Double height;

    @Min(value = 0, message = "Age must be >= 0")
    @Max(value = 120, message = "Age must be <= 120")
    Integer age;

    @Size(max = 50)
    String sportPosition;

    FitnessStatus fitnessStatus;

    @Size(max = 100)
    String emergencyContact;

    @Pattern(regexp = "^\\d{8,}$", message = "Emergency phone should contain at least 8 digits")
    String emergencyPhone;

    @Pattern(regexp = "^(A\\+|A-|B\\+|B-|AB\\+|AB-|O\\+|O-)$", message = "Invalid blood type")
    String bloodType;

    @Size(max = 500)
    String allergies;

    @Size(max = 500)
    String medicalConditions;
}