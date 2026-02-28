package tn.esprit._4se2.pi.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import tn.esprit._4se2.pi.dto.FavoriteDTOs;
import tn.esprit._4se2.pi.repository.FavoriteCategoryRepository;
import tn.esprit._4se2.pi.repository.FavoriteRepository;
import tn.esprit._4se2.pi.repository.ProductRepository;
import tn.esprit._4se2.pi.repository.UserRepository;
import tn.esprit._4se2.pi.entities.Favorite;
import tn.esprit._4se2.pi.entities.FavoriteCategory;
import tn.esprit._4se2.pi.entities.Product;
import tn.esprit._4se2.pi.mappers.FavoriteMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import tn.esprit._4se2.pi.entities.User;
@Service
@AllArgsConstructor
public class FavoriteServiceImpl implements IFavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final FavoriteCategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final FavoriteMapper favoriteMapper;


    @Override
    public FavoriteDTOs.FavoriteResponse addToFavorites(Long userId, FavoriteDTOs.FavoriteRequest request) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }

        Product product = productRepository.findByIdAndDeletedFalse(request.getProductId()).orElse(null);
        if (product == null) {
            return null;
        }

        if (favoriteRepository.existsByUserIdAndProductId(userId, request.getProductId())) {
            return null;
        }

        FavoriteCategory category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId()).orElse(null);
            if (category != null && !category.getUser().getId().equals(userId)) {
                category = null;
            }
        }

        Favorite favorite = Favorite.builder()
                .user(user)
                .product(product)
                .category(category)
                .addedAt(LocalDateTime.now())
                .build();

        Favorite saved = favoriteRepository.save(favorite);
        return favoriteMapper.toDTO(saved);
    }

    @Override
    public List<FavoriteDTOs.FavoriteResponse> addMultipleToFavorites(Long userId, List<FavoriteDTOs.FavoriteRequest> requests) {
        return requests.stream()
                .map(request -> addToFavorites(userId, request))
                .filter(response -> response != null)
                .collect(Collectors.toList());
    }

    @Override
    public void removeFromFavorites(Long userId, Long productId) {
        Optional<Favorite> favorite = favoriteRepository.findByUserIdAndProductId(userId, productId);
        favorite.ifPresent(fav -> favoriteRepository.delete(fav));
    }

    @Override
    public void removeAllFavorites(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            Page<Favorite> favorites = favoriteRepository.findByUser(user, Pageable.unpaged());
            favoriteRepository.deleteAll(favorites.getContent());
        });
    }

    @Override
    public Page<FavoriteDTOs.FavoriteResponse> getUserFavorites(Long userId, Pageable pageable) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            return favoriteRepository.findByUser(userOpt.get(), pageable)
                    .map(favoriteMapper::toDTO);
        }
        return Page.empty();
    }

    @Override
    public List<FavoriteDTOs.FavoriteResponse> getUserFavoritesByCategory(Long userId, Long categoryId) {
        return favoriteRepository.findByUserIdAndCategoryId(userId, categoryId).stream()
                .map(favoriteMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public FavoriteDTOs.FavoriteResponse getFavoriteById(Long id) {
        return favoriteRepository.findById(id)
                .map(favoriteMapper::toDTO)
                .orElse(null);
    }

    @Override
    public FavoriteDTOs.FavoriteCategoryResponse addCategory(Long userId, FavoriteDTOs.FavoriteCategoryRequest request) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }

        if (categoryRepository.existsByUserIdAndName(userId, request.getName())) {
            return null;
        }

        FavoriteCategory category = FavoriteCategory.builder()
                .name(request.getName())
                .user(user)
                .createdAt(LocalDateTime.now())
                .build();

        FavoriteCategory saved = categoryRepository.save(category);
        return favoriteMapper.toCategoryDTO(saved);
    }

    @Override
    public List<FavoriteDTOs.FavoriteCategoryResponse> getUserCategories(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            return categoryRepository.findByUser(userOpt.get()).stream()
                    .map(favoriteMapper::toCategoryDTO)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    @Override
    public void categorizeFavorite(Long favoriteId, Long categoryId) {
        Optional<Favorite> optFavorite = favoriteRepository.findById(favoriteId);
        Optional<FavoriteCategory> optCategory = categoryRepository.findById(categoryId);

        if (optFavorite.isPresent() && optCategory.isPresent()) {
            Favorite favorite = optFavorite.get();
            FavoriteCategory category = optCategory.get();

            if (favorite.getUser().getId().equals(category.getUser().getId())) {
                favorite.setCategory(category);
                favoriteRepository.save(favorite);
            }
        }
    }

    @Override
    public void removeFromCategory(Long favoriteId) {
        favoriteRepository.findById(favoriteId).ifPresent(favorite -> {
            favorite.setCategory(null);
            favoriteRepository.save(favorite);
        });
    }

    @Override
    public boolean isProductInFavorites(Long userId, Long productId) {
        return favoriteRepository.existsByUserIdAndProductId(userId, productId);
    }

    @Override
    public long countUserFavorites(Long userId) {  // Note: méthode correcte "countUserFavorites" pas "countUsers"
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            return favoriteRepository.findByUser(userOpt.get(), Pageable.unpaged()).getTotalElements();
        }
        return 0L;
    }

    @Override
    public long getFavoritesCountByProduct(Long productId) {
        return favoriteRepository.countByProductId(productId);
    }
}
