package tn.esprit._4se2.pi.restcontrollers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import tn.esprit._4se2.pi.dto.Sponsor.FavoriteDTOs;
import tn.esprit._4se2.pi.services.Favorite.IFavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import tn.esprit._4se2.pi.repositories.UserRepository;
import tn.esprit._4se2.pi.entities.User;
import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = {"*"})
@Tag(name = "Favorites Management", description = "Endpoints for managing user favorites")
public class FavoriteRestController {

    private final IFavoriteService favoriteService;
    private final UserRepository userRepository;

    // Fallback ID pour tests ou invités
    private final Long FALLBACK_USER_ID = 1L;

    private Long getCurrentUserId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        if (email == null || "anonymousUser".equals(email)) {
            return FALLBACK_USER_ID;
        }
        return userRepository.findByEmail(email)
                .map(User::getId)
                .orElse(FALLBACK_USER_ID);
    }

    @GetMapping
    @Operation(summary = "Get my favorites", description = "Returns current user's favorites")
    public ResponseEntity<Page<FavoriteDTOs.FavoriteResponse>> getMyFavorites(Pageable pageable) {
        return ResponseEntity.ok(favoriteService.getUserFavorites(getCurrentUserId(), pageable));
    }

    @GetMapping("/categories")
    @Operation(summary = "Get my favorite categories", description = "Returns current user's favorite categories")
    public ResponseEntity<List<FavoriteDTOs.FavoriteCategoryResponse>> getMyCategories() {
        return ResponseEntity.ok(favoriteService.getUserCategories(getCurrentUserId()));
    }

    @GetMapping("/check/{productId}")
    @Operation(summary = "Check if product is in favorites", description = "Returns true if product is in user's favorites")
    public ResponseEntity<Boolean> isProductInFavorites(@PathVariable Long productId) {
        return ResponseEntity.ok(favoriteService.isProductInFavorites(getCurrentUserId(), productId));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get favorites by category", description = "Returns favorites in a specific category")
    public ResponseEntity<List<FavoriteDTOs.FavoriteResponse>> getFavoritesByCategory(
            @PathVariable Long categoryId) {
        return ResponseEntity.ok(favoriteService.getUserFavoritesByCategory(getCurrentUserId(), categoryId));
    }

    @PostMapping
    @Operation(summary = "Add to favorites", description = "Adds a product to user's favorites")
    public ResponseEntity<FavoriteDTOs.FavoriteResponse> addToFavorites(
            @Valid @RequestBody FavoriteDTOs.FavoriteRequest request) {
        return new ResponseEntity<>(favoriteService.addToFavorites(getCurrentUserId(), request), HttpStatus.CREATED);
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Remove from favorites", description = "Removes a product from user's favorites")
    public ResponseEntity<Void> removeFromFavorites(@PathVariable Long productId) {
        favoriteService.removeFromFavorites(getCurrentUserId(), productId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/categories")
    @Operation(summary = "Create favorite category", description = "Creates a new favorite category")
    public ResponseEntity<FavoriteDTOs.FavoriteCategoryResponse> createCategory(
            @Valid @RequestBody FavoriteDTOs.FavoriteCategoryRequest request) {
        return new ResponseEntity<>(favoriteService.addCategory(getCurrentUserId(), request), HttpStatus.CREATED);
    }

    @PutMapping("/{favoriteId}/categorize/{categoryId}")
    @Operation(summary = "Categorize favorite", description = "Assigns a favorite to a category")
    public ResponseEntity<Void> categorizeFavorite(
            @PathVariable Long favoriteId,
            @PathVariable Long categoryId) {
        favoriteService.categorizeFavorite(favoriteId, categoryId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{favoriteId}/categorize")
    @Operation(summary = "Remove from category", description = "Removes a favorite from its category")
    public ResponseEntity<Void> removeFromCategory(@PathVariable Long favoriteId) {
        favoriteService.removeFromCategory(favoriteId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search favorites", description = "Search favorites by product name and category name")
    public ResponseEntity<List<FavoriteDTOs.FavoriteResponse>> searchFavorites(
            @RequestParam(required = false, defaultValue = "") String productName,
            @RequestParam(required = false, defaultValue = "") String categoryName) {
        return ResponseEntity.ok(favoriteService.searchFavorites(productName, categoryName));
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Get low stock favorites", description = "Returns products in favorites that are almost out of stock")
    public ResponseEntity<List<FavoriteDTOs.FavoriteResponse>> getLowStockFavorites() {
        return ResponseEntity.ok(favoriteService.getLowStockFavorites(getCurrentUserId()));
    }

    @PostMapping("/trigger-check")
    @Operation(summary = "Trigger stock check", description = "Manually triggers the low stock notification check (for testing)")
    public ResponseEntity<Void> triggerStockCheck() {
        favoriteService.triggerStockCheck();
        return ResponseEntity.ok().build();
    }
}