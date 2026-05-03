package tn.esprit._4se2.pi.dto.Loyalty;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class LoyaltyTransactionResponse {
    private Long id;
    private Long clientId;
    private Integer points;
    private String reason;
    private String relatedEntityType;
    private Long relatedEntityId;
    private LocalDateTime transactionDate;
}