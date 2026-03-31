package tn.esprit._4se2.pi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

public class FavoriteDTOs {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FavoriteRequest {
        private Long productId;
        private Long categoryId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FavoriteResponse {
        private Long id;
        private ProductDTOs.ProductResponse product;
        private FavoriteCategoryResponse category;
        private LocalDateTime addedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FavoriteCategoryRequest {
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FavoriteCategoryResponse {
        private Long id;
        private String name;
        private int favoritesCount;
        private LocalDateTime createdAt;
    }
}
