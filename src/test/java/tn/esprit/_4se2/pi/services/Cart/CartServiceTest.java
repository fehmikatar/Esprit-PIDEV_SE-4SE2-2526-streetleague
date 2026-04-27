package tn.esprit._4se2.pi.services.Cart;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit._4se2.pi.dto.Sponsor.CartDTOs;
import tn.esprit._4se2.pi.entities.Cart;
import tn.esprit._4se2.pi.entities.CartItem;
import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.mappers.CartMapper;
import tn.esprit._4se2.pi.repositories.CartRepository;
import tn.esprit._4se2.pi.repositories.UserRepository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CartMapper cartMapper;

    @InjectMocks
    private CartService cartService;

    private User testUser;
    private Cart testCart;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        testCart = Cart.builder()
                .id(100L)
                .user(testUser)
                .status(Cart.CartStatus.ACTIVE)
                .total(BigDecimal.ZERO)
                .items(new java.util.ArrayList<>())
                .build();
    }

    @Test
    void checkoutCart_WithCardPayment_ShouldSaveCardDetails() {
        // Arrange
        CartDTOs.CheckoutRequest request = CartDTOs.CheckoutRequest.builder()
                .clientName("John Doe")
                .clientAddress("123 Street")
                .clientPostalCode("12345")
                .clientCity("City")
                .clientPhone("12345678")
                .deliveryMode("LIVRAISON_DOMICILE")
                .paymentMode("CARTE")
                .cardNumber("1234567890123456")
                .expiryDate("12/26")
                .cvv("12345678")
                .clientEmail("john@example.com")
                .build();

        // Add an item to cart to allow checkout
        testCart.getItems().add(new CartItem());
        
        when(cartRepository.findByUserIdAndStatus(1L, Cart.CartStatus.ACTIVE))
                .thenReturn(Optional.of(testCart));
        when(cartRepository.saveAndFlush(any(Cart.class))).thenReturn(testCart);
        when(cartMapper.toDTO(any(Cart.class))).thenReturn(new CartDTOs.CartResponse());

        // Act
        CartDTOs.CartResponse response = cartService.checkoutCart(1L, request);

        // Assert
        assertNotNull(response);
        assertEquals(Cart.CartStatus.CONVERTED, testCart.getStatus());
        assertEquals("CARTE", testCart.getPaymentMode());
        assertEquals("1234567890123456", testCart.getCardNumber());
        assertEquals("12/26", testCart.getExpiryDate());
        assertEquals("12345678", testCart.getCvv());
        assertTrue(testCart.getOrderCode().startsWith("ORD-"));
        
        verify(cartRepository, times(1)).saveAndFlush(testCart);
    }

    @Test
    void checkoutCart_EmptyCart_ShouldReturnNull() {
        // Arrange
        CartDTOs.CheckoutRequest request = new CartDTOs.CheckoutRequest();
        when(cartRepository.findByUserIdAndStatus(1L, Cart.CartStatus.ACTIVE))
                .thenReturn(Optional.of(testCart)); // Cart is empty by default

        // Act
        CartDTOs.CartResponse response = cartService.checkoutCart(1L, request);

        // Assert
        assertNull(response);
    }

    @Test
    void updateOrderStatus_ValidCart_ShouldUpdateStatus() {
        // Arrange
        testCart.setStatus(Cart.CartStatus.CONVERTED);
        when(cartRepository.findById(100L)).thenReturn(Optional.of(testCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(testCart);

        // Act
        cartService.updateOrderStatus(100L, "EXPEDIE");

        // Assert
        assertEquals("EXPEDIE", testCart.getDeliveryStatus());
        verify(cartRepository).save(testCart);
    }

    @Test
    void getAllOrders_ShouldCallRepository() {
        // Arrange
        when(cartRepository.findByStatusOrderByLastModifiedDesc(Cart.CartStatus.CONVERTED))
                .thenReturn(Collections.singletonList(testCart));

        // Act
        List<CartDTOs.CartResponse> orders = cartService.getAllOrders();

        // Assert
        assertNotNull(orders);
        verify(cartRepository).findByStatusOrderByLastModifiedDesc(Cart.CartStatus.CONVERTED);
    }
}
