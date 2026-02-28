package tn.esprit._4se2.pi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tn.esprit._4se2.pi.dto.FavoriteDTOs;

import java.util.List;

public interface IFavoriteService {
    FavoriteDTOs.FavoriteResponse addToFavorites(Long userId, FavoriteDTOs.FavoriteRequest request);
    List<FavoriteDTOs.FavoriteResponse> addMultipleToFavorites(Long userId, List<FavoriteDTOs.FavoriteRequest> requests);

    void removeFromFavorites(Long userId, Long productId);
    void removeAllFavorites(Long userId);

    Page<FavoriteDTOs.FavoriteResponse> getUserFavorites(Long userId, Pageable pageable);
    List<FavoriteDTOs.FavoriteResponse> getUserFavoritesByCategory(Long userId, Long categoryId);
    FavoriteDTOs.FavoriteResponse getFavoriteById(Long id);

    FavoriteDTOs.FavoriteCategoryResponse addCategory(Long userId, FavoriteDTOs.FavoriteCategoryRequest request);
    List<FavoriteDTOs.FavoriteCategoryResponse> getUserCategories(Long userId);

    void categorizeFavorite(Long favoriteId, Long categoryId);
    void removeFromCategory(Long favoriteId);

    boolean isProductInFavorites(Long userId, Long productId);
    long countUserFavorites(Long userId);
    long getFavoritesCountByProduct(Long productId);
}
