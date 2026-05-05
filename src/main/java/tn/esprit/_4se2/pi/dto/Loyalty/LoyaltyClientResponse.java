package tn.esprit._4se2.pi.dto.Loyalty;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class LoyaltyClientResponse {
    private Long id;
    private Long userId;
    private String userFullName;   // à renseigner via UserService
    private Long currentTierId;
    private String currentTierName;
    private Integer totalPoints;
    private LocalDateTime joinedAt;
    private LocalDateTime tierValidUntil;
}