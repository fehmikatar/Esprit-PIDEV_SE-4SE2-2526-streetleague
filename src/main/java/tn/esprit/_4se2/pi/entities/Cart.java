package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "order_code", unique = true)
    String orderCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    List<CartItem> items = new ArrayList<>();

    @PositiveOrZero(message = "Le total ne peut pas être négatif")
    @Column(precision = 10, scale = 2)
    BigDecimal total;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promo_code_id")
    PromoCode appliedPromoCode;

    @Enumerated(EnumType.STRING)
    CartStatus status;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "last_modified")
    LocalDateTime lastModified;

    // Checkout Fields
    @Column(name = "client_name")
    String clientName;

    @Column(name = "client_address")
    String clientAddress;

    @Column(name = "client_postal_code")
    String clientPostalCode;

    @Column(name = "client_city")
    String clientCity;

    @Column(name = "client_phone")
    String clientPhone;

    @Column(name = "delivery_mode")
    String deliveryMode; // LIVRAISON_DOMICILE, RETRAIT_MAGASIN

    @Column(name = "payment_mode")
    String paymentMode; // CARTE, ESPECE

    @PositiveOrZero(message = "Les frais de livraison ne peuvent pas être négatifs")
    @Column(name = "delivery_fee", precision = 10, scale = 2)
    BigDecimal deliveryFee;

    @Column(name = "delivery_status")
    String deliveryStatus; // EN_COURS_DE_TRAITEMENT, EXPEDIE, LIVRE

    // Credit Card Details (Optional, only for CARTE mode)
    @Column(name = "card_number")
    String cardNumber;

    @Column(name = "expiry_date")
    String expiryDate;

    @Column(name = "cvv")
    String cvv;

    @Column(name = "client_email")
    String clientEmail;

    public enum CartStatus {
        ACTIVE, ABANDONED, CONVERTED
    }

    // Méthode métier pour calculer le total (appelée manuellement dans les services)
    public void calculateTotal() {
        if (this.items == null) {
            this.total = BigDecimal.ZERO;
            return;
        }
        this.total = this.items.stream()
                .filter(item -> item.getPrice() != null && item.getQuantity() != null)
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (appliedPromoCode != null && appliedPromoCode.isValid()) {
            this.total = appliedPromoCode.applyDiscount(this.total);
        }
    }
}
