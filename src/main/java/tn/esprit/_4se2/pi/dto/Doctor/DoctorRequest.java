package tn.esprit._4se2.pi.dto.Doctor;

import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DoctorRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50)
    String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50)
    String lastName;

    @NotBlank(message = "Specialty is required")
    @Size(min = 2, max = 100)
    String specialty;

    @NotBlank(message = "License number is required")
    @Size(min = 5, max = 50)
    String licenseNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\d{8,}$", message = "Phone number should contain at least 8 digits")
    String phoneNumber;

    @Size(max = 255)
    String address;
}