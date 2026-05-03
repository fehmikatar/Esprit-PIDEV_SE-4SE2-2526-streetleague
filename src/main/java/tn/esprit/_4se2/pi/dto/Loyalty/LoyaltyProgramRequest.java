package tn.esprit._4se2.pi.dto.Loyalty;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyProgramRequest {
    @NotBlank(message = "Program name is required")
    @Size(max = 100)
    private String name;

    private String description;

    @DecimalMin(value = "0.0")
    private Double pointsPerCurrencyUnit;
}