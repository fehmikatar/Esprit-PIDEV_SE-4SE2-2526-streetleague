package tn.esprit._4se2.pi.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.dto.FavoriteDTOs;
import tn.esprit._4se2.pi.entities.Favorite;
import tn.esprit._4se2.pi.entities.FavoriteCategory;

@Component
@RequiredArgsConstructor
public class FavoriteMapper {

    private final ProductMapper productMapper;

    public FavoriteDTOs.FavoriteResponse toDTO(Favorite favorite) {
        if (favorite == null) return null;

        FavoriteDTOs.FavoriteCategoryResponse categoryResponse = null;
        if (favorite.getCategory() != null) {
            categoryResponse = FavoriteDTOs.FavoriteCategoryResponse.builder()
                    .id(favorite.getCategory().getId())
                    .name(favorite.getCategory().getName())
                    .build();
        }

        return FavoriteDTOs.FavoriteResponse.builder()
                .id(favorite.getId())
                .product(productMapper.toDTO(favorite.getProduct()))
                .category(categoryResponse)
                .addedAt(favorite.getAddedAt())
                .build();
    }

    public FavoriteDTOs.FavoriteCategoryResponse toCategoryDTO(FavoriteCategory category) {
        if (category == null) return null;

        return FavoriteDTOs.FavoriteCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .favoritesCount(category.getFavorites() != null ? category.getFavorites().size() : 0)
                .createdAt(category.getCreatedAt())
                .build();
    }

    public FavoriteCategory toCategoryEntity(FavoriteDTOs.FavoriteCategoryRequest request) {
        if (request == null) return null;

        return FavoriteCategory.builder()
                .name(request.getName())
                .build();
    }
}