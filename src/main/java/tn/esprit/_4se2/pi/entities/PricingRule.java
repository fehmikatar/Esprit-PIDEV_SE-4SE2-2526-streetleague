package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pricing_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PricingRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    Long sportSpaceId;

    @Column(nullable = false)
    String ruleName;

    @Column(nullable = false)
    String ruleType;

    @Column(nullable = false, precision = 10, scale = 2)
    BigDecimal basePrice;

    @Column(precision = 5, scale = 2)
    BigDecimal discountPercentage;

    Integer minBookingHours;

    Integer maxBookingHours;

    @Column(name = "is_active")
    @Builder.Default
    Boolean isActive = true;

    @Column(name = "effective_from")
    LocalDateTime effectiveFrom;

    @Column(name = "effective_to")
    LocalDateTime effectiveTo;

    @Column(name = "created_at")
    LocalDateTime createdAt;
}