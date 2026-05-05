package tn.esprit._4se2.pi.entities;


import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class Cart {

    private Long id;
    private String orderCode;
    private List<CartItem> items;  // Assuming CartItem is another class
    private BigDecimal total;
    private BigDecimal deliveryFee;
    private String appliedPromoCode;
    private CartStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;
    private String clientName;
    private String clientAddress;
    private String clientPostalCode;
    private String clientCity;
    private String clientPhone;
    private String deliveryMode;
    private String paymentMode;
    private String cardNumber;
    private String expiryDate;
    private String cvv;
    private String clientEmail;
    private String deliveryConfirmationCode;

    // Method to get the items in the cart
    public List<CartItem> getItems() {
        return items;
    }

    // Method to get the total price of the cart
    public BigDecimal getTotal() {
        return total;
    }

    // Method to get the delivery fee
    public BigDecimal getDeliveryFee() {
        return deliveryFee;
    }

    // Method to get the applied promo code
    public String getAppliedPromoCode() {
        return appliedPromoCode;
    }

    // Method to get the cart status
    public CartStatus getStatus() {
        return status;
    }

    // Method to get the creation date of the cart
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    // Method to get the last modified date of the cart
    public LocalDateTime getLastModified() {
        return lastModified;
    }

    // Method to get the client's name
    public String getClientName() {
        return clientName;
    }

    // Method to get the client's address
    public String getClientAddress() {
        return clientAddress;
    }

    // Method to get the client's postal code
    public String getClientPostalCode() {
        return clientPostalCode;
    }

    // Method to get the client's city
    public String getClientCity() {
        return clientCity;
    }

    // Method to get the client's phone
    public String getClientPhone() {
        return clientPhone;
    }

    // Method to get the delivery mode
    public String getDeliveryMode() {
        return deliveryMode;
    }

    // Method to get the payment mode
    public String getPaymentMode() {
        return paymentMode;
    }

    // Method to get the card number
    public String getCardNumber() {
        return cardNumber;
    }

    // Method to get the expiry date
    public String getExpiryDate() {
        return expiryDate;
    }

    // Method to get the CVV
    public String getCvv() {
        return cvv;
    }

    // Method to get the client's email
    public String getClientEmail() {
        return clientEmail;
    }

    // Method to get the delivery confirmation code
    public String getDeliveryConfirmationCode() {
        return deliveryConfirmationCode;
    }
}
