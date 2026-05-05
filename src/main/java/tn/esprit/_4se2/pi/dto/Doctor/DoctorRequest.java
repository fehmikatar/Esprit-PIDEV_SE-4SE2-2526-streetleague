package tn.esprit._4se2.pi.dto.Doctor;

import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DoctorRequest {

    @NotBlank(message = "First name is required")
    String firstName;

    @NotBlank(message = "Last name is required")
    String lastName;

    @NotBlank(message = "Specialty is required")
    String specialty;

    @NotBlank(message = "License number is required")
    String licenseNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    String email;

    @NotBlank(message = "Phone number is required")
    String phoneNumber;

    String address;
    @NotBlank(message = "Working hours start is required")
    String workingHoursStart;

    @NotBlank(message = "Working hours end is required")
    String workingHoursEnd;
    
    @JsonProperty("isAvailable")
    boolean available;
}
