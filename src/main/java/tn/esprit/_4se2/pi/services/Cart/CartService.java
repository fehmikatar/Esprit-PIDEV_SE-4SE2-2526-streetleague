package tn.esprit._4se2.pi.services.Cart;  // Note: service en minuscule

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit._4se2.pi.dto.Sponsor.CartDTOs;
import tn.esprit._4se2.pi.dto.Sponsor.ProductDTOs;
import tn.esprit._4se2.pi.repositories.*;
import tn.esprit._4se2.pi.entities.*;
import tn.esprit._4se2.pi.mappers.CartMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.repositories.UserRepository;

@Service
@AllArgsConstructor
@Transactional
public class CartService implements ICartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductVariantRepository productVariantRepository;
    private final PromoCodeRepository promoCodeRepository;
    private final SavedCartRepository savedCartRepository;
    private final CartMapper cartMapper;
    private final tn.esprit._4se2.pi.services.WebSocket.WebSocketNotificationService webSocketNotificationService;
    private final org.springframework.web.client.RestTemplate restTemplate;

    private Cart getOrCreateCart(Long userId) {
        return cartRepository.findByUserIdAndStatus(userId, Cart.CartStatus.ACTIVE)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId).orElse(null);
                    if (user == null) return null;

                    LocalDateTime now = LocalDateTime.now();
                    Cart newCart = Cart.builder()
                            .user(user)
                            .total(BigDecimal.ZERO)
                            .status(Cart.CartStatus.ACTIVE)
                            .createdAt(now)
                            .lastModified(now)
                            .build();

                    newCart.calculateTotal();
                    return cartRepository.save(newCart);
                });
    }

    @Override
    public CartDTOs.CartResponse getCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return cart != null ? cartMapper.toDTO(cart) : null;
    }

    @Override
    public CartDTOs.CartResponse getCartById(Long cartId) {
        return cartRepository.findById(cartId)
                .map(cartMapper::toDTO)
                .orElse(null);
    }

    @Override
    public CartDTOs.CartResponse addToCart(Long userId, CartDTOs.AddToCartRequest request) {
        if (request.getQuantity() == null || request.getQuantity() <= 0) return null;

        Cart cart = getOrCreateCart(userId);
        if (cart == null) return null;

        Product product = productRepository.findByIdAndDeletedFalse(request.getProductId()).orElse(null);
        if (product == null) return null;
        
        // Enforce logical status: Only EN_STOCK items can be added to cart
        if (product.getStatus() != null && product.getStatus() != Product.ProductStatus.EN_STOCK) {
            throw new IllegalStateException("Product is not available for purchase (Status: " + product.getStatus() + ")");
        }
        
        if (product.getStock() < request.getQuantity()) return null;

        ProductVariant variant = null;
        if (request.getVariantId() != null) {
            variant = productVariantRepository.findById(request.getVariantId()).orElse(null);
            if (variant == null || !variant.getProduct().getId().equals(product.getId()) || variant.getStock() < request.getQuantity()) return null;
        }

        final ProductVariant finalVariant = variant;
        CartItem existingItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()) &&
                        ((finalVariant == null && item.getSelectedVariant() == null) ||
                                (finalVariant != null && item.getSelectedVariant() != null && item.getSelectedVariant().getId().equals(finalVariant.getId()))))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            int newQuantity = existingItem.getQuantity() + request.getQuantity();
            if (product.getStock() < newQuantity) return null;
            existingItem.setQuantity(newQuantity);
            cartItemRepository.save(existingItem);
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .selectedVariant(variant)
                    .quantity(request.getQuantity())
                    .price(variant != null && variant.getPriceAdjustment() != null ?
                            product.getPrix().add(variant.getPriceAdjustment()) : product.getPrix())
                    .addedAt(LocalDateTime.now())
                    .build();

            cart.getItems().add(newItem);
            cartItemRepository.save(newItem);
        }

        cart.setLastModified(LocalDateTime.now());
        cart.calculateTotal();
        Cart updated = cartRepository.save(cart);

        return cartMapper.toDTO(updated);
    }

    @Override
    public CartDTOs.CartResponse addMultipleToCart(Long userId, List<CartDTOs.AddToCartRequest> requests) {
        CartDTOs.CartResponse result = null;
        for (CartDTOs.AddToCartRequest request : requests) {
            result = addToCart(userId, request);
            if (result == null) break;
        }
        return result;
    }

    @Override
    public CartDTOs.CartResponse updateCartItem(Long userId, Long itemId, CartDTOs.UpdateCartItemRequest request) {
        Cart cart = getOrCreateCart(userId);
        if (cart == null) return null;

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElse(null);

        if (item == null) return cartMapper.toDTO(cart);

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            cart.getItems().remove(item);
            cartItemRepository.delete(item);
        } else {
            if (item.getProduct().getStock() < request.getQuantity()) return null;
            item.setQuantity(request.getQuantity());
            cartItemRepository.save(item);
        }

        cart.setLastModified(LocalDateTime.now());
        cart.calculateTotal();
        Cart updated = cartRepository.save(cart);
        return cartMapper.toDTO(updated);
    }

    @Override
    public CartDTOs.CartResponse removeFromCart(Long userId, Long itemId) {
        Cart cart = getOrCreateCart(userId);
        if (cart == null) return null;

        cart.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .ifPresent(item -> {
                    cart.getItems().remove(item);
                    cartItemRepository.delete(item);
                });

        cart.setLastModified(LocalDateTime.now());
        cart.calculateTotal();
        Cart updated = cartRepository.save(cart);
        return cartMapper.toDTO(updated);
    }
    @Override
    public CartDTOs.CartResponse clearCart(Long userId) {
        Cart cart = getOrCreateCart(userId);
        if (cart == null) return null;

        cartItemRepository.deleteAllByCartId(cart.getId());
        cart.getItems().clear();
        cart.setTotal(BigDecimal.ZERO);
        cart.setAppliedPromoCode(null);
        cart.setLastModified(LocalDateTime.now());

        Cart updated = cartRepository.save(cart);
        return cartMapper.toDTO(updated);
    }

    @Override
    public CartDTOs.CartResponse checkoutCart(Long userId, CartDTOs.CheckoutRequest request) {
        Cart cart = getOrCreateCart(userId);
        if (cart == null || cart.getItems().isEmpty()) return null;
        
        // Final logical check: Ensure all items are still EN_STOCK before finalizing order
        for (CartItem item : cart.getItems()) {
            if (item.getProduct().getStatus() != Product.ProductStatus.EN_STOCK) {
                throw new IllegalStateException("Item '" + item.getProduct().getNom() + "' is no longer in stock.");
            }
        }

        cart.setClientName(request.getClientName());
        cart.setClientAddress(request.getClientAddress());
        cart.setClientPostalCode(request.getClientPostalCode());
        cart.setClientCity(request.getClientCity());
        cart.setClientPhone(request.getClientPhone());
        cart.setDeliveryMode(request.getDeliveryMode());
        cart.setPaymentMode(request.getPaymentMode());

        cart.calculateTotal(); // Calculate base total

        BigDecimal deliveryFee = BigDecimal.ZERO;
        if ("LIVRAISON_DOMICILE".equals(request.getDeliveryMode())) {
            // Free delivery above 300 DT
            if (cart.getTotal().compareTo(BigDecimal.valueOf(300)) < 0) {
                // RECALCULATE DYNAMICALLY IN BACKEND FOR SECURITY
                String fullAddr = (request.getClientAddress() + " " + request.getClientCity()).trim();
                deliveryFee = calculateDeliveryFee(fullAddr);
            }
        }
        cart.setDeliveryFee(deliveryFee);
        cart.setTotal(cart.getTotal().add(deliveryFee));
        cart.setDeliveryStatus("EN_COURS_DE_TRAITEMENT");
        
        // Save Payment Details
        if ("CARTE".equals(request.getPaymentMode())) {
            cart.setCardNumber(request.getCardNumber());
            cart.setExpiryDate(request.getExpiryDate());
            cart.setCvv(request.getCvv());
            cart.setClientEmail(request.getClientEmail());
        }

        // Automate Order Code Generation (Format: SL-YYYYMMDD-RAND-ID)
        String datePart = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd").format(java.time.LocalDate.now());
        String randomPart = java.util.UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        cart.setOrderCode("SL-" + datePart + "-" + randomPart + "-" + cart.getId());

        cart.setStatus(Cart.CartStatus.CONVERTED);
        cart.setDeliveryConfirmationCode(UUID.randomUUID().toString());
        cart.setLastModified(LocalDateTime.now());

        Cart updated = cartRepository.saveAndFlush(cart);
        return cartMapper.toDTO(updated);
    }

    @Override
    public List<CartDTOs.CartResponse> getMyOrders(Long userId) {
        return cartRepository.findByUserIdAndStatusOrderByLastModifiedDesc(userId, Cart.CartStatus.CONVERTED)
                .stream()
                .map(cartMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CartDTOs.CartResponse> getAllOrders() {
        return cartRepository.findAll().stream()
                .filter(c -> c.getStatus() == Cart.CartStatus.CONVERTED)
                .map(c -> {
                    if (c.getDeliveryConfirmationCode() == null) {
                        c.setDeliveryConfirmationCode(java.util.UUID.randomUUID().toString());
                        cartRepository.save(c);
                    }
                    return cartMapper.toDTO(c);
                })
                .collect(Collectors.toList());
    }

    @Override
    public CartDTOs.CartResponse updateOrderStatus(Long cartId, String newStatus) {
        Cart cart = cartRepository.findById(cartId).orElse(null);
        if (cart == null || cart.getStatus() != Cart.CartStatus.CONVERTED) return null;

        cart.setDeliveryStatus(newStatus);
        
        cart.setLastModified(LocalDateTime.now());
        Cart updated = cartRepository.save(cart);
        return cartMapper.toDTO(updated);
    }

    @Override
    public CartDTOs.CartResponse confirmDelivery(String confirmationCode) {
        Cart cart = cartRepository.findByDeliveryConfirmationCode(confirmationCode).orElse(null);
        if (cart == null || "LIVRE".equals(cart.getDeliveryStatus())) return null;

        cart.setDeliveryStatus("LIVRE");
        cart.setLastModified(LocalDateTime.now());
        
        Cart updated = cartRepository.save(cart);
        System.out.println("LOG DELIVERY: Order " + cart.getOrderCode() + " confirmed via QR Code.");
        
        // Notify all connected clients (especially admins) that the order is delivered
        webSocketNotificationService.sendOrderUpdateNotification(cart.getId(), "LIVRE", cart.getOrderCode());
        
        return cartMapper.toDTO(updated);
    }

    @Override
    public CartDTOs.CartResponse getCartByConfirmationCode(String confirmationCode) {
        return cartRepository.findByDeliveryConfirmationCode(confirmationCode)
                .map(cartMapper::toDTO)
                .orElse(null);
    }

    @Override
    public CartDTOs.CartResponse applyPromoCode(Long userId, String promoCode) {
        Cart cart = getOrCreateCart(userId);
        if (cart == null) return null;

        PromoCode code = promoCodeRepository.findByCode(promoCode).orElse(null);
        if (code == null || !code.isValid()) return null;

        cart.setAppliedPromoCode(code);
        cart.setLastModified(LocalDateTime.now());
        cart.calculateTotal();

        code.setTimesUsed(code.getTimesUsed() + 1);
        promoCodeRepository.save(code);

        Cart updated = cartRepository.save(cart);
        return cartMapper.toDTO(updated);
    }

    @Override
    public CartDTOs.CartResponse removePromoCode(Long userId) {
        Cart cart = getOrCreateCart(userId);
        if (cart == null) return null;

        cart.setAppliedPromoCode(null);
        cart.setLastModified(LocalDateTime.now());
        cart.calculateTotal();

        Cart updated = cartRepository.save(cart);
        return cartMapper.toDTO(updated);
    }

    @Override
    public CartDTOs.CartResponse saveCurrentCart(Long userId, String cartName) {
        Cart cart = getOrCreateCart(userId);
        if (cart == null || cart.getItems().isEmpty()) return null;

        boolean exists = savedCartRepository.findByUserId(userId).stream()
                .anyMatch(sc -> sc.getName().equals(cartName));

        if (exists) return null;

        SavedCart savedCart = SavedCart.builder()
                .name(cartName)
                .user(cart.getUser())
                .cartContents("Cart_" + cart.getId() + "_" + System.currentTimeMillis())
                .createdAt(LocalDateTime.now())
                .build();

        savedCartRepository.save(savedCart);

        return cartMapper.toDTO(cart);
    }

    @Override
    public List<CartDTOs.SavedCartResponse> getSavedCarts(Long userId) {
        return savedCartRepository.findByUserId(userId).stream()
                .map(sc -> CartDTOs.SavedCartResponse.builder()
                        .id(sc.getId())
                        .name(sc.getName())
                        .createdAt(sc.getCreatedAt())
                        .shareToken(sc.getShareToken())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public CartDTOs.CartResponse loadSavedCart(Long userId, Long savedCartId) {
        SavedCart savedCart = savedCartRepository.findById(savedCartId).orElse(null);
        if (savedCart == null || !savedCart.getUser().getId().equals(userId)) return null;

        // TODO: Désérialiser le panier
        return CartDTOs.CartResponse.builder()
                .items(List.of())
                .subtotal(BigDecimal.ZERO)
                .discount(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .build();
    }

    @Override
    public String shareCart(Long userId, Long savedCartId) {
        SavedCart savedCart = savedCartRepository.findById(savedCartId).orElse(null);
        if (savedCart == null || !savedCart.getUser().getId().equals(userId)) return null;

        String shareToken = UUID.randomUUID().toString();
        savedCart.setShareToken(shareToken);
        savedCartRepository.save(savedCart);

        return shareToken;
    }

    @Override
    public CartDTOs.CartResponse loadSharedCart(String shareToken) {
        SavedCart savedCart = savedCartRepository.findByShareToken(shareToken).orElse(null);
        if (savedCart == null) return null;

        // TODO: Désérialiser le panier
        return CartDTOs.CartResponse.builder()
                .items(List.of())
                .subtotal(BigDecimal.ZERO)
                .discount(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .build();
    }

    @Override
    public void deleteSavedCart(Long userId, Long savedCartId) {
        savedCartRepository.findById(savedCartId).ifPresent(savedCart -> {
            if (savedCart.getUser().getId().equals(userId)) {
                savedCartRepository.delete(savedCart);
            }
        });
    }

    @Override
    public List<ProductDTOs.ProductResponse> getCartRecommendations(Long userId) {
        return new ArrayList<>();
    }

    @Override
    public Integer predictAbandonmentRisk(Long userId) {
        return 0;
    }

    @Override
    public int getCartItemsCount(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return cart != null ? cart.getItems().size() : 0;
    }

    @Override
    public BigDecimal getCartTotal(Long userId) {
        Cart cart = getOrCreateCart(userId);
        return cart != null ? cart.getTotal() : BigDecimal.ZERO;
    }
    @Override
    public BigDecimal calculateDeliveryFee(String address) {
        if (address == null || address.trim().isEmpty()) {
            return BigDecimal.valueOf(7.0);
        }

        String addrLower = address.toLowerCase().trim();
        BigDecimal finalFee = BigDecimal.valueOf(7.0); // Default fallback

        // 1. Check for 12 DT Cities (Sud / Lointain)
        if (addrLower.contains("jerba") || addrLower.contains("tataouine") || addrLower.contains("tozeur") || 
            addrLower.contains("gabes") || addrLower.contains("gabès") || addrLower.contains("medenine") || 
            addrLower.contains("médenine") || addrLower.contains("douz")) {
            finalFee = BigDecimal.valueOf(12.0);
        }
        // 2. Check for 9 DT Cities (Sfax, Gafsa, etc.)
        else if (addrLower.contains("sfax") || addrLower.contains("gafsa") || addrLower.contains("jendouba") || 
                 addrLower.contains("kasserine") || addrLower.contains("bouzid") || addrLower.contains("kef")) {
            finalFee = BigDecimal.valueOf(9.0);
        }
        // 3. Check for 7 DT Cities (Sahel / Centre)
        else if (addrLower.contains("sousse") || addrLower.contains("monastir") || addrLower.contains("mahdia") || 
                 addrLower.contains("kairouan") || addrLower.contains("beja") || addrLower.contains("béja") || 
                 addrLower.contains("siliana")) {
            finalFee = BigDecimal.valueOf(7.0);
        }
        // 4. Check for 5 DT Cities (Nord / Cap Bon)
        else if (addrLower.contains("bizerte") || addrLower.contains("nabeul") || addrLower.contains("hammamet") || 
                 addrLower.contains("zaghouan") || addrLower.contains("kebili")) {
            finalFee = BigDecimal.valueOf(5.0);
        }
        // 5. Check for 3 DT Cities (Grand Tunis)
        else if (addrLower.contains("tunis") || addrLower.contains("ariana") || addrLower.contains("arous") || 
                 addrLower.contains("manouba")) {
            finalFee = BigDecimal.valueOf(3.0);
        }

        System.out.println("LOG DELIVERY: Address='" + address + "' -> Fee=" + finalFee);
        return finalFee;
    }

    private double calculateDistanceHelper(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Rayon terre en km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}