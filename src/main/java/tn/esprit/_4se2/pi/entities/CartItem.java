package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cart_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    Product product;

    @NotNull(message = "La quantité est obligatoire")
    @Positive(message = "La quantité doit être strictement positive")
    @Column(nullable = false)
    Integer quantity;

    @NotNull(message = "Le prix ne peut pas être nul")
    @PositiveOrZero(message = "Le prix ne peut pas être négatif")
    @Column(precision = 10, scale = 2)
    BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id")
    ProductVariant selectedVariant;

    @Column(name = "added_at")
    LocalDateTime addedAt;
}
