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
public class PlayerRequest {

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

    @NotNull(message = "Skill level is required")
    @Min(1) @Max(5)
    Integer skillLevel;

    @NotBlank(message = "Position is required")
    String position;

    Long teamId;

    @Min(0)
    Integer gamesPlayed;

    @DecimalMin("0.0") @DecimalMax("5.0")
    Double rating;
}