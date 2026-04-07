package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "promo_codes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PromoCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique = true, nullable = false)
    String code;

    @Enumerated(EnumType.STRING)
    DiscountType discountType;

    @Column(precision = 10, scale = 2)
    BigDecimal discountValue;

    LocalDateTime expiryDate;

    Integer usageLimit;

    @Builder.Default
    Integer timesUsed = 0;

    @Column(nullable = false)
    @Builder.Default
    Boolean active = true;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    public enum DiscountType {
        PERCENTAGE, FIXED_AMOUNT
    }

    public boolean isValid() {
        return active &&
                (expiryDate == null || expiryDate.isAfter(LocalDateTime.now())) &&
                (usageLimit == null || timesUsed < usageLimit);
    }

    public BigDecimal applyDiscount(BigDecimal amount) {
        if (!isValid()) return amount;

        if (discountType == DiscountType.PERCENTAGE) {
            return amount.multiply(BigDecimal.ONE.subtract(
                    discountValue.divide(BigDecimal.valueOf(100))));
        } else {
            return amount.subtract(discountValue).max(BigDecimal.ZERO);
        }
    }
}
