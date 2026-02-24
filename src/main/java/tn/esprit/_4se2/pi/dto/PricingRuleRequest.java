package tn.esprit._4se2.pi.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PricingRuleRequest {

    @NotNull(message = "Sport Space ID is required")
    Long sportSpaceId;

    @NotBlank(message = "Rule name is required")
    String ruleName;

    @NotBlank(message = "Rule type is required")
    String ruleType; // HOURLY, DAILY, WEEKLY, PACKAGE

    @NotNull(message = "Base price is required")
    @DecimalMin("0.0")
    BigDecimal basePrice;

    @DecimalMin("0.0") @DecimalMax("100.0")
    BigDecimal discountPercentage;

    @Min(1)
    Integer minBookingHours;

    @Min(1)
    Integer maxBookingHours;

    @NotNull
    Boolean isActive;

    @NotNull(message = "Effective from is required")
    LocalDateTime effectiveFrom;

    LocalDateTime effectiveTo;
}