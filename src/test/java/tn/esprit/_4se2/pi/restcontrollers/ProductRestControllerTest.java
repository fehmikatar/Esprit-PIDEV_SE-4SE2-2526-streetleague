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
import tn.esprit._4se2.pi.dto.Sponsor.ProductDTOs;
import tn.esprit._4se2.pi.services.Product.IProductService;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProductRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IProductService productService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct_InvalidRequest_ShouldReturnBadRequest() throws Exception {
        // Arrange
        ProductDTOs.ProductRequest invalidRequest = new ProductDTOs.ProductRequest();
        invalidRequest.setNom(""); // Blank name
        invalidRequest.setPrix(BigDecimal.valueOf(-10)); // Negative price

        // Act & Assert
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void createProduct_AsUser_ShouldReturnForbidden() throws Exception {
        // Arrange
        ProductDTOs.ProductRequest request = new ProductDTOs.ProductRequest();
        request.setNom("Test Product");

        // Act & Assert
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
