package tn.esprit._4se2.pi.dto.Sponsor;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PromoCodeDTO {
    private Long id;
    private String code;
    private String discountType; // "PERCENTAGE" or "FIXED_AMOUNT"
    private BigDecimal discountValue;
    private LocalDateTime expiryDate;
    private Integer usageLimit;
    private Integer timesUsed;
    private Boolean active;
    private LocalDateTime createdAt;
}