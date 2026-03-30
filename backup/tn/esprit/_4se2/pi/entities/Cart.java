package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    List<CartItem> items = new ArrayList<>();

    @Column(precision = 10, scale = 2)
    BigDecimal total;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promo_code_id")
    PromoCode appliedPromoCode;

    @Enumerated(EnumType.STRING)
    CartStatus status;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "last_modified")
    LocalDateTime lastModified;

    public enum CartStatus {
        ACTIVE, ABANDONED, CONVERTED
    }

    public void calculateTotal() {
        this.total = items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (appliedPromoCode != null && appliedPromoCode.isValid()) {
            this.total = appliedPromoCode.applyDiscount(this.total);
        }
    }
}