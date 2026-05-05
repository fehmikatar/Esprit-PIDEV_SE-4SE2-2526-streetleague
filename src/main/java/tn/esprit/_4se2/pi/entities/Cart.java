package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL)
    private List<CartItem> items;

    private Double deliveryFee;
    private String appliedPromoCode;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;

    private String clientName;
    private String clientAddress;
    private String clientPostalCode;
    private String clientCity;
    private String clientPhone;
    private String clientEmail;

    private String deliveryMode;
    private String paymentMode;
    private String cardNumber;
    private String expiryDate;
    private String cvv;
    private String deliveryConfirmationCode;

    // Constructeur par défaut
    public Cart() {}

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public List<CartItem> getItems() { return items; }
    public void setItems(List<CartItem> items) { this.items = items; }

    public Double getDeliveryFee() { return deliveryFee; }
    public void setDeliveryFee(Double deliveryFee) { this.deliveryFee = deliveryFee; }

    public String getAppliedPromoCode() { return appliedPromoCode; }
    public void setAppliedPromoCode(String appliedPromoCode) { this.appliedPromoCode = appliedPromoCode; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastModified() { return lastModified; }
    public void setLastModified(LocalDateTime lastModified) { this.lastModified = lastModified; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getClientAddress() { return clientAddress; }
    public void setClientAddress(String clientAddress) { this.clientAddress = clientAddress; }

    public String getClientPostalCode() { return clientPostalCode; }
    public void setClientPostalCode(String clientPostalCode) { this.clientPostalCode = clientPostalCode; }

    public String getClientCity() { return clientCity; }
    public void setClientCity(String clientCity) { this.clientCity = clientCity; }

    public String getClientPhone() { return clientPhone; }
    public void setClientPhone(String clientPhone) { this.clientPhone = clientPhone; }

    public String getClientEmail() { return clientEmail; }
    public void setClientEmail(String clientEmail) { this.clientEmail = clientEmail; }

    public String getDeliveryMode() { return deliveryMode; }
    public void setDeliveryMode(String deliveryMode) { this.deliveryMode = deliveryMode; }

    public String getPaymentMode() { return paymentMode; }
    public void setPaymentMode(String paymentMode) { this.paymentMode = paymentMode; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public String getCvv() { return cvv; }
    public void setCvv(String cvv) { this.cvv = cvv; }

    public String getDeliveryConfirmationCode() { return deliveryConfirmationCode; }
    public void setDeliveryConfirmationCode(String deliveryConfirmationCode) { this.deliveryConfirmationCode = deliveryConfirmationCode; }
}
