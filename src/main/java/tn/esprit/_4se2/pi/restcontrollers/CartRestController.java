package tn.esprit._4se2.pi.restcontrollers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import tn.esprit._4se2.pi.dto.Sponsor.CartDTOs;
import tn.esprit._4se2.pi.dto.Sponsor.ProductDTOs;
import tn.esprit._4se2.pi.services.Cart.ICartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import tn.esprit._4se2.pi.repositories.UserRepository;
import tn.esprit._4se2.pi.entities.User;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = {"*"})
@Tag(name = "Cart Management", description = "Endpoints for managing shopping cart")
public class CartRestController {

    private final ICartService cartService;
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
    @Operation(summary = "Get current cart", description = "Returns current user's cart")
    public ResponseEntity<CartDTOs.CartResponse> getCart() {
        return ResponseEntity.ok(cartService.getCart(getCurrentUserId()));
    }

    @PostMapping("/items")
    @Operation(summary = "Add to cart", description = "Adds a product to the cart")
    public ResponseEntity<CartDTOs.CartResponse> addToCart(
            @Valid @RequestBody CartDTOs.AddToCartRequest request) {
        return ResponseEntity.ok(cartService.addToCart(getCurrentUserId(), request));
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update cart item", description = "Updates quantity of a cart item")
    public ResponseEntity<CartDTOs.CartResponse> updateCartItem(
            @PathVariable Long itemId,
            @Valid @RequestBody CartDTOs.UpdateCartItemRequest request) {
        return ResponseEntity.ok(cartService.updateCartItem(getCurrentUserId(), itemId, request));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(summary = "Remove from cart", description = "Removes an item from the cart")
    public ResponseEntity<CartDTOs.CartResponse> removeFromCart(@PathVariable Long itemId) {
        return ResponseEntity.ok(cartService.removeFromCart(getCurrentUserId(), itemId));
    }

    @DeleteMapping
    @Operation(summary = "Clear cart", description = "Removes all items from the cart")
    public ResponseEntity<CartDTOs.CartResponse> clearCart() {
        return ResponseEntity.ok(cartService.clearCart(getCurrentUserId()));
    }

    @PostMapping("/checkout")
    @Operation(summary = "Checkout cart", description = "Finalizes the cart with delivery and payment coordinates")
    public ResponseEntity<?> checkoutCart(
            @Valid @RequestBody CartDTOs.CheckoutRequest request) {
        try {
            CartDTOs.CartResponse response = cartService.checkoutCart(getCurrentUserId(), request);
            if (response == null) {
                return ResponseEntity.badRequest().body(java.util.Map.of("error", "Le panier est vide ou introuvable."));
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(java.util.Map.of("error", "ERREUR_BACKEND: " + e.getMessage() + " / Cause: " + (e.getCause() != null ? e.getCause().getMessage() : "null")));
        }
    }

    @GetMapping("/orders/my")
    @Operation(summary = "Get my orders")
    public ResponseEntity<List<CartDTOs.CartResponse>> getMyOrders() {
        return ResponseEntity.ok(cartService.getMyOrders(getCurrentUserId()));
    }

    @GetMapping("/orders/all")
    @Operation(summary = "Get all orders (Admin)")
    public ResponseEntity<List<CartDTOs.CartResponse>> getAllOrders() {
        return ResponseEntity.ok(cartService.getAllOrders());
    }

    @PutMapping("/orders/{cartId}/status")
    @Operation(summary = "Update order status (Admin)")
    public ResponseEntity<CartDTOs.CartResponse> updateOrderStatus(
            @PathVariable Long cartId,
            @Valid @RequestBody CartDTOs.UpdateOrderStatusRequest request) {
        CartDTOs.CartResponse response = cartService.updateOrderStatus(cartId, request.getStatus());
        if (response == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/promo")
    @Operation(summary = "Apply promo code", description = "Applies a promo code to the cart")
    public ResponseEntity<CartDTOs.CartResponse> applyPromoCode(
            @Valid @RequestBody CartDTOs.PromoCodeRequest request) {
        return ResponseEntity.ok(cartService.applyPromoCode(getCurrentUserId(), request.getCode()));
    }

    @DeleteMapping("/promo")
    @Operation(summary = "Remove promo code", description = "Removes the applied promo code")
    public ResponseEntity<CartDTOs.CartResponse> removePromoCode() {
        return ResponseEntity.ok(cartService.removePromoCode(getCurrentUserId()));
    }

    @PostMapping("/save")
    @Operation(summary = "Save current cart", description = "Saves the current cart with a name")
    public ResponseEntity<CartDTOs.CartResponse> saveCart(@RequestParam String name) {
        return ResponseEntity.ok(cartService.saveCurrentCart(getCurrentUserId(), name));
    }

    @GetMapping("/saved")
    @Operation(summary = "Get saved carts", description = "Returns all saved carts of the user")
    public ResponseEntity<List<CartDTOs.SavedCartResponse>> getSavedCarts() {
        return ResponseEntity.ok(cartService.getSavedCarts(getCurrentUserId()));
    }

    @PostMapping("/saved/{savedCartId}/load")
    @Operation(summary = "Load saved cart", description = "Loads a saved cart")
    public ResponseEntity<CartDTOs.CartResponse> loadSavedCart(@PathVariable Long savedCartId) {
        return ResponseEntity.ok(cartService.loadSavedCart(getCurrentUserId(), savedCartId));
    }

    @DeleteMapping("/saved/{savedCartId}")
    @Operation(summary = "Delete saved cart", description = "Deletes a saved cart")
    public ResponseEntity<Void> deleteSavedCart(@PathVariable Long savedCartId) {
        cartService.deleteSavedCart(getCurrentUserId(), savedCartId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/saved/{savedCartId}/share")
    @Operation(summary = "Share cart", description = "Generates a share token for a saved cart")
    public ResponseEntity<String> shareCart(@PathVariable Long savedCartId) {
        return ResponseEntity.ok(cartService.shareCart(getCurrentUserId(), savedCartId));
    }

    @GetMapping("/shared/{shareToken}")
    @Operation(summary = "Load shared cart", description = "Loads a cart shared via token")
    public ResponseEntity<CartDTOs.CartResponse> loadSharedCart(@PathVariable String shareToken) {
        return ResponseEntity.ok(cartService.loadSharedCart(shareToken));
    }

    @GetMapping("/recommendations")
    @Operation(summary = "Get cart recommendations", description = "Returns product recommendations based on cart")
    public ResponseEntity<List<ProductDTOs.ProductResponse>> getCartRecommendations() {
        return ResponseEntity.ok(cartService.getCartRecommendations(getCurrentUserId()));
    }

    @GetMapping("/abandonment-risk")
    @Operation(summary = "Get abandonment risk", description = "Predicts cart abandonment risk (0-100)")
    public ResponseEntity<Integer> getAbandonmentRisk() {
        return ResponseEntity.ok(cartService.predictAbandonmentRisk(getCurrentUserId()));
    }

    @GetMapping("/count")
    @Operation(summary = "Get cart items count", description = "Returns the number of items in cart")
    public ResponseEntity<Integer> getCartItemsCount() {
        return ResponseEntity.ok(cartService.getCartItemsCount(getCurrentUserId()));
    }

    @GetMapping("/total")
    @Operation(summary = "Get cart total", description = "Returns the total amount of the cart")
    public ResponseEntity<BigDecimal> getCartTotal() {
        return ResponseEntity.ok(cartService.getCartTotal(getCurrentUserId()));
    }

    @GetMapping("/calculate-delivery")
    @Operation(summary = "Calculate delivery fee", description = "Calculates fee based on address")
    public ResponseEntity<BigDecimal> calculateDelivery(@RequestParam String address) {
        return ResponseEntity.ok(cartService.calculateDeliveryFee(address));
    }

    @GetMapping("/confirm-delivery/{code}")
    @Operation(summary = "Get delivery details for confirmation", description = "Shows order info and confirmation button")
    public ResponseEntity<String> confirmDelivery(@PathVariable String code) {
        CartDTOs.CartResponse response = cartService.getCartByConfirmationCode(code);
        if (response == null) {
            return ResponseEntity.status(404).body("<html><body style='font-family: sans-serif; text-align: center; padding: 50px;'><h1 style='color: #e74c3c;'>Code invalide</h1><p>Ce code de confirmation est introuvable.</p></body></html>");
        }
        
        if ("LIVRE".equals(response.getDeliveryStatus())) {
            return ResponseEntity.ok("<html><body style='font-family: sans-serif; text-align: center; padding: 50px;'><h1 style='color: #27ae60;'>Déjà Livrée</h1><p>Cette commande a déjà été confirmée comme livrée.</p></body></html>");
        }

        String html = "<html><body style='font-family: sans-serif; background: #f8fafc; display: flex; justify-content: center; align-items: center; height: 100vh; margin: 0;'>" +
                "<div style='background: white; padding: 40px; border-radius: 24px; box-shadow: 0 20px 40px rgba(0,0,0,0.1); text-align: center; max-width: 400px; width: 90%;'>" +
                "<div style='font-size: 48px; margin-bottom: 20px;'>📦</div>" +
                "<h1 style='margin: 0 0 10px 0; color: #1e293b; font-size: 24px;'>Confirmer la Livraison</h1>" +
                "<p style='color: #64748b; margin-bottom: 30px;'>Commande: <strong>" + response.getOrderCode() + "</strong><br>Client: " + response.getClientName() + "<br>Adresse: " + response.getClientAddress() + "</p>" +
                "<form method='POST' action='/api/cart/confirm-delivery/" + code + "'>" +
                "<button type='submit' style='background: #22c55e; color: white; border: none; padding: 16px 32px; border-radius: 12px; font-weight: bold; font-size: 16px; cursor: pointer; width: 100%; box-shadow: 0 10px 15px -3px rgba(34, 197, 94, 0.4); transition: transform 0.2s;'>✅ CONFIRMER LA RÉCEPTION</button>" +
                "</form>" +
                "<p style='margin-top: 20px; font-size: 12px; color: #94a3b8;'>En cliquant sur ce bouton, vous confirmez que le colis a été remis en mains propres.</p>" +
                "</div>" +
                "</body></html>";
        
        return ResponseEntity.ok(html);
    }

    @PostMapping("/confirm-delivery/{code}")
    @Operation(summary = "Perform delivery confirmation", description = "Triggered by the button on the confirmation page")
    public ResponseEntity<String> confirmDeliveryPost(@PathVariable String code) {
        CartDTOs.CartResponse response = cartService.confirmDelivery(code);
        if (response == null) {
            return ResponseEntity.status(404).body("<html><body style='font-family: sans-serif; text-align: center; padding: 50px;'><h1 style='color: #e74c3c;'>Erreur</h1><p>Impossible de confirmer la livraison.</p></body></html>");
        }
        return ResponseEntity.ok("<html><body style='font-family: sans-serif; text-align: center; padding: 50px;'><h1 style='color: #27ae60;'>Livraison Confirmée !</h1><p>La commande <strong>" + response.getOrderCode() + "</strong> est maintenant livrée.</p></body></html>");
    }
}