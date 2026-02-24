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
public class UserRequest {

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
    @Pattern(regexp = "^\\d{8,}$", message = "Phone should contain at least 8 digits")
    String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 255)
    String password;
}