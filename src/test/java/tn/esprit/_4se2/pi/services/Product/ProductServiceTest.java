package tn.esprit._4se2.pi.services.Product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit._4se2.pi.dto.Sponsor.ProductDTOs;
import tn.esprit._4se2.pi.entities.Product;
import tn.esprit._4se2.pi.mappers.ProductMapper;
import tn.esprit._4se2.pi.repositories.ProductRepository;
import tn.esprit._4se2.pi.repositories.CategoryRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = Product.builder()
                .id(1L)
                .nom("Test Product")
                .description("Test description")
                .prix(BigDecimal.valueOf(100.0))
                .stock(10)
                .deleted(false)
                .build();
    }

    @Test
    void addProduct_DuplicateName_ShouldReturnNull() {
        // Arrange
        ProductDTOs.ProductRequest request = new ProductDTOs.ProductRequest();
        request.setNom("Test Product");
        
        when(productRepository.existsByNomAndDeletedFalse("Test Product")).thenReturn(true);

        // Act
        ProductDTOs.ProductResponse response = productService.addProduct(request);

        // Assert
        assertNull(response);
    }

    @Test
    void deleteProduct_ShouldSetDeletedTrue() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        
        // Act
        productService.deleteProductById(1L);

        // Assert
        assertTrue(testProduct.getDeleted());
        verify(productRepository).save(testProduct);
    }

    @Test
    void updateStock_ValidQuantity_ShouldDecreaseStock() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        
        // Act
        productService.updateStock(1L, 3);

        // Assert
        assertEquals(7, testProduct.getStock());
        verify(productRepository).save(testProduct);
    }

    @Test
    void updateStock_InsufficientStock_ShouldNotUpdate() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        
        // Act
        productService.updateStock(1L, 15);

        // Assert
        assertEquals(10, testProduct.getStock()); // No change
        verify(productRepository, never()).save(any(Product.class));
    }
}
