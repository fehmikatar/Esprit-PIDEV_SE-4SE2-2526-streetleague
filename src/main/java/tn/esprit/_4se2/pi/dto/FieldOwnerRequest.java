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
public class FieldOwnerRequest {

    @NotBlank(message = "First name is required")
    String firstName;

    @NotBlank(message = "Last name is required")
    String lastName;

    @NotBlank(message = "Email is required")
    @Email
    String email;

    @NotBlank(message = "Phone is required")
    String phone;

    @NotBlank(message = "Password is required")
    String password;

    @NotBlank(message = "Business name is required")
    String businessName;

    @NotBlank(message = "Business license is required")
    String businessLicense;

    @NotBlank(message = "Location is required")
    String location;

    @DecimalMin("0.0")
    Double totalRevenue;
}