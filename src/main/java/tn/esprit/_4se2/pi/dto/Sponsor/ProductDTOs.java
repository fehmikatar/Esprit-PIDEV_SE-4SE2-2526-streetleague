package tn.esprit._4se2.pi.dto.Sponsor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.constraints.*;

public class ProductDTOs {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductRequest {
        @NotBlank(message = "Le nom du produit est obligatoire")
        private String nom;

        private String marque;

        @NotBlank(message = "La description est obligatoire")
        private String description;

        @NotNull(message = "Le prix est obligatoire")
        @Positive(message = "Le prix doit être strictement positif")
        private BigDecimal prix;

        @NotNull(message = "Le stock est obligatoire")
        @PositiveOrZero(message = "Le stock ne peut pas être négatif")
        private Integer stock;

        private List<String> images;

        @NotNull(message = "La catégorie est obligatoire")
        private Long categoryId;

        private List<ProductVariantDTO> variants;
        private String status;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductResponse {
        private Long id;
        private String nom;
        private String marque;
        private String description;
        private BigDecimal prix;
        private Integer stock;
        private List<String> images;
        private CategoryDTO category;
        private List<ProductVariantDTO> variants;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Boolean deleted;
        private String status;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductVariantDTO {
        private Long id;

        @Size(max = 50)
        private String size;

        @Size(max = 50)
        private String color;

        @NotBlank(message = "Le SKU est obligatoire pour la variante")
        private String sku;

        @NotNull(message = "Le stock de la variante est obligatoire")
        @PositiveOrZero(message = "Le stock ne peut pas être négatif")
        private Integer stock;

        private BigDecimal priceAdjustment;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductHighDemandDTO {
        private Long id;
        private String nom;
        private Long quantityInActiveCarts;
        private Integer currentStock;
        private String categoryName;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductSearchCriteria {
        private String keyword;
        private Long categoryId;
        private BigDecimal minPrice;
        private BigDecimal maxPrice;
    }
}