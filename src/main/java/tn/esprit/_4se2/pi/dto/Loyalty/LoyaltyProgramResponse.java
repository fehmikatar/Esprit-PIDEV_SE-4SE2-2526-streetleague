package tn.esprit._4se2.pi.dto.Loyalty;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class LoyaltyProgramResponse {
    private Long id;
    private String name;
    private String description;
    private Boolean active;
    private Double pointsPerCurrencyUnit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}