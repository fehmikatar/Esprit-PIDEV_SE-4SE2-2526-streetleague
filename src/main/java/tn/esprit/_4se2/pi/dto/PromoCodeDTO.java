package tn.esprit._4se2.pi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromoCodeDTO {
    private Long id;
    private String code;
    private String discountType;
    private BigDecimal discountValue;
    private LocalDateTime expiryDate;
    private Integer usageLimit;
    private Integer timesUsed;
    private Boolean active;
    private LocalDateTime createdAt;
}
