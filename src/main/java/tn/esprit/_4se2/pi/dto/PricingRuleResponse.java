package tn.esprit._4se2.pi.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PricingRuleResponse {
    Long id;
    Long sportSpaceId;
    String sportSpaceName;
    String ruleName;
    String ruleType;
    BigDecimal basePrice;
    BigDecimal discountPercentage;
    Integer minBookingHours;
    Integer maxBookingHours;
    Boolean isActive;
    LocalDateTime effectiveFrom;
    LocalDateTime effectiveTo;
    LocalDateTime createdAt;
}