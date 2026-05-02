package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;
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
    public enum ProductStatus {
        EN_STOCK,
        EN_ARRIVAGE,
        RUPTURE_DE_STOCK
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String nom;

    @Column(nullable = false, length = 1000)
    String description;

    @Column(nullable = false, precision = 10, scale = 2)
    BigDecimal prix;

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

    @Column(nullable = false)
    @Builder.Default
    Boolean deleted = false;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "updated_at")
    LocalDateTime updatedAt;

    @Transient
    @Builder.Default
    ProductStatus status = ProductStatus.EN_STOCK;

    @ManyToMany(mappedBy = "products")
    @Builder.Default
    private List<Performance> performances = new ArrayList<>();

    public ProductStatus getStatus() {
        if (status != null) {
            return status;
        }

        if (stock == null || stock <= 0) {
            return ProductStatus.RUPTURE_DE_STOCK;
        }

        return ProductStatus.EN_STOCK;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }
}
