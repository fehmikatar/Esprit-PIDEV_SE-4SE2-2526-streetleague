package tn.esprit._4se2.pi.mappers;

import tn.esprit._4se2.pi.dto.Sponsor.CartDTOs;
import tn.esprit._4se2.pi.entities.Cart;
import tn.esprit._4se2.pi.entities.CartItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CartMapper {

    private final ProductMapper productMapper;

    public CartDTOs.CartResponse toDTO(Cart cart) {
        if (cart == null) return null;

        var items = cart.getItems().stream()
                .map(this::toItemDTO)
                .collect(Collectors.toList());

        BigDecimal subtotal = items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Ensure total is never null, fallback to subtotal
        BigDecimal total = (cart.getTotal() != null && cart.getTotal().compareTo(BigDecimal.ZERO) > 0) 
                           ? cart.getTotal() : subtotal;
        
        // Add delivery fee to total if it's not already there (for fallbacks)
        if (cart.getDeliveryFee() != null && cart.getTotal() == null) {
            total = total.add(cart.getDeliveryFee());
        }

        return CartDTOs.CartResponse.builder()
                .id(cart.getId())
                .orderCode(cart.getOrderCode() != null ? cart.getOrderCode() : "ORD-" + cart.getId())
                .items(items)
                .subtotal(subtotal)
                .discount(subtotal.subtract(total).max(BigDecimal.ZERO))
                .total(total)
                .appliedPromoCode(cart.getAppliedPromoCode() != null ?
                        cart.getAppliedPromoCode().getCode() : null)
                .status(cart.getStatus() != null ? cart.getStatus().name() : "ACTIVE")
                .createdAt(cart.getCreatedAt() != null ? cart.getCreatedAt() : LocalDateTime.now())
                .lastModified(cart.getLastModified())
                .clientName(cart.getClientName() != null ? cart.getClientName() : "Client Inconnu")
                .clientAddress(cart.getClientAddress())
                .clientPostalCode(cart.getClientPostalCode())
                .clientCity(cart.getClientCity())
                .clientPhone(cart.getClientPhone() != null ? cart.getClientPhone() : "N/A")
                .deliveryMode(cart.getDeliveryMode())
                .paymentMode(cart.getPaymentMode())
                .deliveryFee(cart.getDeliveryFee())
                .deliveryStatus(cart.getDeliveryStatus() != null ? cart.getDeliveryStatus() : "EN_COURS_DE_TRAITEMENT")
                .cardNumber(cart.getCardNumber())
                .expiryDate(cart.getExpiryDate())
                .cvv(cart.getCvv())
                .clientEmail(cart.getClientEmail())
                .deliveryConfirmationCode(cart.getDeliveryConfirmationCode())
                .build();
    }

    public CartDTOs.CartItemDTO toItemDTO(CartItem item) {
        if (item == null) return null;

        String productImage = null;
        if (item.getProduct().getImages() != null && !item.getProduct().getImages().isEmpty()) {
            tn.esprit._4se2.pi.entities.ProductImage pi = item.getProduct().getImages().get(0);
            if (pi.getUploadImage() != null && !pi.getUploadImage().trim().isEmpty()) {
                productImage = pi.getUploadImage();
            } else if (pi.getImageUrl() != null && !pi.getImageUrl().trim().isEmpty()) {
                productImage = pi.getImageUrl();
            }
        }

        return CartDTOs.CartItemDTO.builder()
                .id(item.getId())
                .productId(item.getProduct().getId())
                .productName(item.getProduct().getNom())
                .productImage(productImage)
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .selectedVariant(productMapper.toVariantDTO(item.getSelectedVariant()))
                .addedAt(item.getAddedAt())
                .build();
    }
}