package tn.esprit._4se2.pi.dto.Sponsor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.validation.constraints.*;

public class CartDTOs {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartResponse {
        private Long id;
        private String orderCode;
        private List<CartItemDTO> items;
        private BigDecimal subtotal;
        private BigDecimal discount;
        private BigDecimal total;
        private String appliedPromoCode;
        private String status;
        private LocalDateTime createdAt;
        private LocalDateTime lastModified;
        private String clientName;
        private String clientAddress;
        private String clientPostalCode;
        private String clientCity;
        private String clientPhone;
        private String deliveryMode;
        private String paymentMode;
        private BigDecimal deliveryFee;
        private String deliveryStatus;
        private String cardNumber;
        private String expiryDate;
        private String cvv;
        private String clientEmail;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CartItemDTO {
        private Long id;
        private Long productId;
        private String productName;
        private String productImage;
        private BigDecimal price;
        private Integer quantity;
        private ProductDTOs.ProductVariantDTO selectedVariant;
        private LocalDateTime addedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddToCartRequest {
        @NotNull(message = "L'ID du produit est obligatoire")
        private Long productId;

        @NotNull(message = "La quantité est obligatoire")
        @Positive(message = "La quantité doit être strictement positive")
        private Integer quantity;

        private Long variantId;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateCartItemRequest {
        @NotNull(message = "La quantité est obligatoire")
        @Positive(message = "La quantité doit être strictement positive")
        private Integer quantity;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PromoCodeRequest {
        @NotBlank(message = "Le code promo ne peut pas être vide")
        @Size(min = 2, max = 50, message = "Le code promo doit avoir entre 2 et 50 caractères")
        private String code;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SavedCartRequest {
        private String name;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SavedCartResponse {
        private Long id;
        private String name;
        private LocalDateTime createdAt;
        private String shareToken;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckoutRequest {
        @NotBlank(message = "Le nom du client est obligatoire")
        private String clientName;

        @NotBlank(message = "L'adresse du client est obligatoire")
        private String clientAddress;

        @NotBlank(message = "Le code postal est obligatoire")
        private String clientPostalCode;

        @NotBlank(message = "La ville est obligatoire")
        private String clientCity;

        @NotBlank(message = "Le numéro de téléphone est obligatoire")
        private String clientPhone;

        @NotBlank(message = "Le mode de livraison est obligatoire")
        private String deliveryMode; // LIVRAISON_DOMICILE, RETRAIT_MAGASIN

        @NotBlank(message = "Le mode de paiement est obligatoire")
        private String paymentMode; // CARTE, ESPECE

        // Payment Details (Required IF paymentMode is CARTE)
        private String cardNumber;
        private String expiryDate;
        private String cvv; // Can be 3-8 digits as requested
        private String clientEmail;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateOrderStatusRequest {
        @NotBlank(message = "Le statut est obligatoire")
        private String status;
    }
}