// LoyaltyTierRequest.java
package tn.esprit._4se2.pi.dto.Loyalty;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@Builder
@NoArgsConstructor @AllArgsConstructor
public class LoyaltyTierRequest {
    @NotNull(message = "Program ID is required")
    private Long programId;

    @NotBlank(message = "Tier name is required")
    @Size(max = 50)
    private String name;

    @NotNull(message = "Level is required")
    @Min(1)
    private Integer level;

    @NotNull(message = "Points required is required")
    @Min(0)
    private Integer pointsRequired;

    @DecimalMin(value = "0.0")
    @DecimalMax(value = "100.0")
    private Double extraDiscountRate;

    private String benefits;
    private String exclusiveAccessTag;
}