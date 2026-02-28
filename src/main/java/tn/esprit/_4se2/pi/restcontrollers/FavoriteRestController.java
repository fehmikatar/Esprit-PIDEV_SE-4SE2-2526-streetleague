package tn.esprit._4se2.pi.restcontrollers;

import tn.esprit._4se2.pi.dto.FavoriteDTOs;
import tn.esprit._4se2.pi.service.IFavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@CrossOrigin(origins = {"*"})
@Tag(name = "Favorites Management", description = "Endpoints for managing user favorites")
public class FavoriteRestController {

    private final IFavoriteService favoriteService;

    // Pour les tests, on utilise un userId fixe (par exemple 1)
    private final Long TEST_USER_ID = 1L;

    @GetMapping
    @Operation(summary = "Get my favorites", description = "Returns current user's favorites")
    public ResponseEntity<Page<FavoriteDTOs.FavoriteResponse>> getMyFavorites(Pageable pageable) {
        return ResponseEntity.ok(favoriteService.getUserFavorites(TEST_USER_ID, pageable));
    }

    @GetMapping("/categories")
    @Operation(summary = "Get my favorite categories", description = "Returns current user's favorite categories")
    public ResponseEntity<List<FavoriteDTOs.FavoriteCategoryResponse>> getMyCategories() {
        return ResponseEntity.ok(favoriteService.getUserCategories(TEST_USER_ID));
    }

    @GetMapping("/check/{productId}")
    @Operation(summary = "Check if product is in favorites", description = "Returns true if product is in user's favorites")
    public ResponseEntity<Boolean> isProductInFavorites(@PathVariable Long productId) {
        return ResponseEntity.ok(favoriteService.isProductInFavorites(TEST_USER_ID, productId));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get favorites by category", description = "Returns favorites in a specific category")
    public ResponseEntity<List<FavoriteDTOs.FavoriteResponse>> getFavoritesByCategory(
            @PathVariable Long categoryId) {
        return ResponseEntity.ok(favoriteService.getUserFavoritesByCategory(TEST_USER_ID, categoryId));
    }

    @PostMapping
    @Operation(summary = "Add to favorites", description = "Adds a product to user's favorites")
    public ResponseEntity<FavoriteDTOs.FavoriteResponse> addToFavorites(
            @Valid @RequestBody FavoriteDTOs.FavoriteRequest request) {
        return new ResponseEntity<>(favoriteService.addToFavorites(TEST_USER_ID, request), HttpStatus.CREATED);
    }

    @DeleteMapping("/{productId}")
    @Operation(summary = "Remove from favorites", description = "Removes a product from user's favorites")
    public ResponseEntity<Void> removeFromFavorites(@PathVariable Long productId) {
        favoriteService.removeFromFavorites(TEST_USER_ID, productId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/categories")
    @Operation(summary = "Create favorite category", description = "Creates a new favorite category")
    public ResponseEntity<FavoriteDTOs.FavoriteCategoryResponse> createCategory(
            @Valid @RequestBody FavoriteDTOs.FavoriteCategoryRequest request) {
        return new ResponseEntity<>(favoriteService.addCategory(TEST_USER_ID, request), HttpStatus.CREATED);
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
}