package tn.esprit._4se2.pi.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdminRequest {

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50)
    String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50)
    String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    String email;

    @NotBlank(message = "Phone is required")
    String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 255)
    String password;

    @NotBlank(message = "Admin role is required")
    String adminRole;

    @NotBlank(message = "Department is required")
    String department;
}