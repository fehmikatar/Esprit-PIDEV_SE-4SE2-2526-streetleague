package tn.esprit._4se2.pi.service;

import tn.esprit._4se2.pi.dto.CartDTOs;
import tn.esprit._4se2.pi.dto.ProductDTOs;
import java.math.BigDecimal;
import java.util.List;

public interface ICartService {
    // Opérations de base
    CartDTOs.CartResponse getCart(Long userId);
    CartDTOs.CartResponse getCartById(Long cartId);

    CartDTOs.CartResponse addToCart(Long userId, CartDTOs.AddToCartRequest request);
    CartDTOs.CartResponse addMultipleToCart(Long userId, List<CartDTOs.AddToCartRequest> requests);

    CartDTOs.CartResponse updateCartItem(Long userId, Long itemId, CartDTOs.UpdateCartItemRequest request);
    CartDTOs.CartResponse removeFromCart(Long userId, Long itemId);
    CartDTOs.CartResponse clearCart(Long userId);

    // Promo codes
    CartDTOs.CartResponse applyPromoCode(Long userId, String promoCode);
    CartDTOs.CartResponse removePromoCode(Long userId);

    // Paniers sauvegardés
    CartDTOs.CartResponse saveCurrentCart(Long userId, String cartName);
    List<CartDTOs.SavedCartResponse> getSavedCarts(Long userId);
    CartDTOs.CartResponse loadSavedCart(Long userId, Long savedCartId);
    String shareCart(Long userId, Long savedCartId);
    CartDTOs.CartResponse loadSharedCart(String shareToken);
    void deleteSavedCart(Long userId, Long savedCartId);

    // Méthodes de base (ex-AI)
    List<ProductDTOs.ProductResponse> getCartRecommendations(Long userId);
    Integer predictAbandonmentRisk(Long userId);

    // Utilitaires
    int getCartItemsCount(Long userId);
    BigDecimal getCartTotal(Long userId);
}