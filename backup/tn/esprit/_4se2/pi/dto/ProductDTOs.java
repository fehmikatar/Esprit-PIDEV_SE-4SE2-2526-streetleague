package tn.esprit._4se2.pi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ProductDTOs {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductRequest {
        private String nom;
        private String description;
        private BigDecimal prix;
        private Integer stock;
        private List<String> images;
        private Long categoryId;
        private List<ProductVariantDTO> variants;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductResponse {
        private Long id;
        private String nom;
        private String description;
        private BigDecimal prix;
        private Integer stock;
        private List<String> images;
        private CategoryDTO category;
        private List<ProductVariantDTO> variants;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private Boolean deleted;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductVariantDTO {
        private Long id;
        private String size;
        private String color;
        private String sku;
        private Integer stock;
        private BigDecimal priceAdjustment;
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