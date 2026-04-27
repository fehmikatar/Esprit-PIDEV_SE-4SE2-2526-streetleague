package tn.esprit._4se2.pi.mappers;

import tn.esprit._4se2.pi.dto.Sponsor.CartDTOs;
import tn.esprit._4se2.pi.entities.Cart;
import tn.esprit._4se2.pi.entities.CartItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
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

        BigDecimal discount = subtotal.subtract(cart.getTotal());

        return CartDTOs.CartResponse.builder()
                .id(cart.getId())
                .orderCode(cart.getOrderCode())
                .items(items)
                .subtotal(subtotal)
                .discount(discount)
                .total(cart.getTotal())
                .appliedPromoCode(cart.getAppliedPromoCode() != null ?
                        cart.getAppliedPromoCode().getCode() : null)
                .status(cart.getStatus() != null ? cart.getStatus().name() : "ACTIVE")
                .createdAt(cart.getCreatedAt())
                .lastModified(cart.getLastModified())
                .clientName(cart.getClientName())
                .clientAddress(cart.getClientAddress())
                .clientPostalCode(cart.getClientPostalCode())
                .clientCity(cart.getClientCity())
                .clientPhone(cart.getClientPhone())
                .deliveryMode(cart.getDeliveryMode())
                .paymentMode(cart.getPaymentMode())
                .deliveryFee(cart.getDeliveryFee())
                .deliveryStatus(cart.getDeliveryStatus())
                .cardNumber(cart.getCardNumber())
                .expiryDate(cart.getExpiryDate())
                .cvv(cart.getCvv())
                .clientEmail(cart.getClientEmail())
                .build();
    }

    public CartDTOs.CartItemDTO toItemDTO(CartItem item) {
        if (item == null) return null;

        String productImage = item.getProduct().getImages() != null &&
                !item.getProduct().getImages().isEmpty() ?
                item.getProduct().getImages().get(0) : null;

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