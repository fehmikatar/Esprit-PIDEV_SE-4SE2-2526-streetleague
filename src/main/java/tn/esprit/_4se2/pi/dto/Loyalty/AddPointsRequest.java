package tn.esprit._4se2.pi.dto.Loyalty;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class AddPointsRequest {
    @NotNull
    private Long userId;

    @NotNull
    @Min(1)
    private Integer points;

    @NotBlank
    private String reason; // "achat", "bonus", etc.

    private String relatedEntityType; // "Order", "Promotion"

    private Long relatedEntityId;
}