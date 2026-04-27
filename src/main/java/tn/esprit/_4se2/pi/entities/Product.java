package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)

public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @NotBlank(message = "Le nom du produit est obligatoire")
    @Size(min = 2, max = 255, message = "Le nom doit comporter entre 2 et 255 caractères")
    @Column(nullable = false)
    String nom;

    @Size(max = 100, message = "La marque ne peut pas dépasser 100 caractères")
    @Column(length = 100)
    String marque;

    @NotBlank(message = "La description est obligatoire")
    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    @Column(nullable = false, length = 1000)
    String description;

    @NotNull(message = "Le prix est obligatoire")
    @Positive(message = "Le prix doit être strictement positif")
    @Column(nullable = false, precision = 10, scale = 2)
    BigDecimal prix;

    @NotNull(message = "Le stock est obligatoire")
    @PositiveOrZero(message = "Le stock ne peut pas être négatif")
    @Column(nullable = false)
    Integer stock;

    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    @Builder.Default
    List<String> images = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    List<ProductVariant> variants = new ArrayList<>();

    public enum ProductStatus {
        EN_STOCK,
        RUPTURE_DE_STOCK,
        EN_ARRIVAGE
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    ProductStatus status = ProductStatus.EN_STOCK;

    @Column(nullable = false)
    @Builder.Default
    Boolean deleted = false;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;
}
