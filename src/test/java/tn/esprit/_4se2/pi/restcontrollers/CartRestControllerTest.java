package tn.esprit._4se2.pi.restcontrollers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit._4se2.pi.dto.Sponsor.CartDTOs;
import tn.esprit._4se2.pi.services.Cart.ICartService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CartRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ICartService cartService;

    @Test
    @WithMockUser
    void checkoutCart_InvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Arrange: Missing mandatory fields like clientName, clientAddress
        CartDTOs.CheckoutRequest invalidRequest = CartDTOs.CheckoutRequest.builder()
                .clientName("") // Blank name
                .clientPhone("123") // Too short
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/cart/checkout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void checkoutCart_ValidRequest_ShouldReturnOk() throws Exception {
        // Arrange
        CartDTOs.CheckoutRequest validRequest = CartDTOs.CheckoutRequest.builder()
                .clientName("John Doe")
                .clientAddress("123 Street")
                .clientPostalCode("12345")
                .clientCity("City")
                .clientPhone("12345678")
                .deliveryMode("RETRAIT_MAGASIN")
                .paymentMode("ESPECE")
                .build();

        // SETUP MOCK
        when(cartService.checkoutCart(anyLong(), any(CartDTOs.CheckoutRequest.class)))
                .thenReturn(new CartDTOs.CartResponse());

        // Act & Assert
        mockMvc.perform(post("/api/cart/checkout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk());
    }
}
