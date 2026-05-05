package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;

@Entity
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productName;
    private int quantity;
    private double price;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    // Constructeur par défaut
    public CartItem() {}

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public Cart getCart() { return cart; }
    public void setCart(Cart cart) { this.cart = cart; }
}
