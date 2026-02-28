package tn.esprit._4se2.pi.restcontrollers;

import tn.esprit._4se2.pi.dto.CartDTOs;
import tn.esprit._4se2.pi.dto.ProductDTOs;
import tn.esprit._4se2.pi.service.ICartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@CrossOrigin(origins = {"*"})
@Tag(name = "Cart Management", description = "Endpoints for managing shopping cart")
public class CartRestController {

    private final ICartService cartService;

    // Pour les tests, on utilise un userId fixe (par exemple 1)
    private final Long TEST_USER_ID = 1L;

    @GetMapping
    @Operation(summary = "Get current cart", description = "Returns current user's cart")
    public ResponseEntity<CartDTOs.CartResponse> getCart() {
        return ResponseEntity.ok(cartService.getCart(TEST_USER_ID));
    }

    @PostMapping("/items")
    @Operation(summary = "Add to cart", description = "Adds a product to the cart")
    public ResponseEntity<CartDTOs.CartResponse> addToCart(
            @Valid @RequestBody CartDTOs.AddToCartRequest request) {
        return ResponseEntity.ok(cartService.addToCart(TEST_USER_ID, request));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update cart item", description = "Updates quantity of a cart item")
    public ResponseEntity<CartDTOs.CartResponse> updateCartItem(
            @PathVariable Long itemId,
            @Valid @RequestBody CartDTOs.UpdateCartItemRequest request) {
        return ResponseEntity.ok(cartService.updateCartItem(TEST_USER_ID, itemId, request));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove from cart", description = "Removes an item from the cart")
    public ResponseEntity<CartDTOs.CartResponse> removeFromCart(@PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeFromCart(TEST_USER_ID, itemId));
    }

    @DeleteMapping
    @Operation(summary = "Clear cart", description = "Removes all items from the cart")
    public ResponseEntity<CartDTOs.CartResponse> clearCart() {
        return ResponseEntity.ok(cartService.clearCart(TEST_USER_ID));
    }

    @PostMapping("/promo")
    @Operation(summary = "Apply promo code", description = "Applies a promo code to the cart")
    public ResponseEntity<CartDTOs.CartResponse> applyPromoCode(
            @RequestBody CartDTOs.PromoCodeRequest request) {
        return ResponseEntity.ok(cartService.applyPromoCode(TEST_USER_ID, request.getCode()));
    }

    @DeleteMapping("/promo")
    @Operation(summary = "Remove promo code", description = "Removes the applied promo code")
    public ResponseEntity<CartDTOs.CartResponse> removePromoCode() {
        return ResponseEntity.ok(cartService.removePromoCode(TEST_USER_ID));
    }

    @PostMapping("/save")
    @Operation(summary = "Save current cart", description = "Saves the current cart with a name")
    public ResponseEntity<CartDTOs.CartResponse> saveCart(@RequestParam String name) {
        return ResponseEntity.ok(cartService.saveCurrentCart(TEST_USER_ID, name));
    }

    @GetMapping("/saved")
    @Operation(summary = "Get saved carts", description = "Returns all saved carts of the user")
    public ResponseEntity<List<CartDTOs.SavedCartResponse>> getSavedCarts() {
        return ResponseEntity.ok(cartService.getSavedCarts(TEST_USER_ID));
    }

    @PostMapping("/saved/{savedCartId}/load")
    @Operation(summary = "Load saved cart", description = "Loads a saved cart")
    public ResponseEntity<CartDTOs.CartResponse> loadSavedCart(@PathVariable Long savedCartId) {
        return ResponseEntity.ok(cartService.loadSavedCart(TEST_USER_ID, savedCartId));
    }

    @DeleteMapping("/saved/{savedCartId}")
    @Operation(summary = "Delete saved cart", description = "Deletes a saved cart")
    public ResponseEntity<Void> deleteSavedCart(@PathVariable Long savedCartId) {
        cartService.deleteSavedCart(TEST_USER_ID, savedCartId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/saved/{savedCartId}/share")
    @Operation(summary = "Share cart", description = "Generates a share token for a saved cart")
    public ResponseEntity<String> shareCart(@PathVariable Long savedCartId) {
        return ResponseEntity.ok(cartService.shareCart(TEST_USER_ID, savedCartId));
    }

    @GetMapping("/shared/{shareToken}")
    @Operation(summary = "Load shared cart", description = "Loads a cart shared via token")
    public ResponseEntity<CartDTOs.CartResponse> loadSharedCart(@PathVariable String shareToken) {
        return ResponseEntity.ok(cartService.loadSharedCart(shareToken));
    }

    @GetMapping("/recommendations")
    @Operation(summary = "Get cart recommendations", description = "Returns product recommendations based on cart")
    public ResponseEntity<List<ProductDTOs.ProductResponse>> getCartRecommendations() {
        return ResponseEntity.ok(cartService.getCartRecommendations(TEST_USER_ID));
    }

    @GetMapping("/abandonment-risk")
    @Operation(summary = "Get abandonment risk", description = "Predicts cart abandonment risk (0-100)")
    public ResponseEntity<Integer> getAbandonmentRisk() {
        return ResponseEntity.ok(cartService.predictAbandonmentRisk(TEST_USER_ID));
    }

    @GetMapping("/count")
    @Operation(summary = "Get cart items count", description = "Returns the number of items in cart")
    public ResponseEntity<Integer> getCartItemsCount() {
        return ResponseEntity.ok(cartService.getCartItemsCount(TEST_USER_ID));
    }

    @GetMapping("/total")
    @Operation(summary = "Get cart total", description = "Returns the total amount of the cart")
    public ResponseEntity<BigDecimal> getCartTotal() {
        return ResponseEntity.ok(cartService.getCartTotal(TEST_USER_ID));
    }
}