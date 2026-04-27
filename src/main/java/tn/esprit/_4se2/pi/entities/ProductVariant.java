package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_variants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Size(max = 50)
    String size;
    
    @Size(max = 50)
    String color;

    @NotBlank(message = "Le SKU est obligatoire")
    @Column(unique = true)
    String sku;

    @NotNull(message = "Le stock du variant est obligatoire")
    @PositiveOrZero(message = "Le stock ne peut pas être négatif")
    Integer stock;

    BigDecimal priceAdjustment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    Product product;

    @Column(name = "created_at")
    LocalDateTime createdAt;
}
