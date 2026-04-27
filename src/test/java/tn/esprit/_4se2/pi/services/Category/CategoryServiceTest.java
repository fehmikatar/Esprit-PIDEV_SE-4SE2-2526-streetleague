package tn.esprit._4se2.pi.services.Category;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit._4se2.pi.dto.categorie.CategoryRequest;
import tn.esprit._4se2.pi.dto.categorie.CategoryResponse;
import tn.esprit._4se2.pi.entities.Category;
import tn.esprit._4se2.pi.entities.SportCommunity;
import tn.esprit._4se2.pi.repositories.CategoryRepository;
import tn.esprit._4se2.pi.repositories.SportCommunityRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private SportCommunityRepository sportCommunityRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category testCategory;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setNom("Football");
        testCategory.setCapacity(22);
    }

    @Test
    void addCategory_ShouldSaveAndCreateCommunity() {
        // Arrange
        CategoryRequest request = new CategoryRequest();
        request.setName("Football");
        request.setCapacity(22);
        
        when(categoryRepository.findByNomIgnoreCase("Football")).thenReturn(Optional.empty());
        when(categoryRepository.save(any(Category.class))).thenReturn(testCategory);
        when(sportCommunityRepository.findBySportCategoryId(any())).thenReturn(Optional.empty());
        when(sportCommunityRepository.save(any(SportCommunity.class))).thenReturn(new SportCommunity());

        // Act
        CategoryResponse response = categoryService.addCategory(request);

        // Assert
        assertNotNull(response);
        assertEquals("Football", response.getName());
        verify(categoryRepository).save(any(Category.class));
        verify(sportCommunityRepository).save(any(SportCommunity.class));
    }

    @Test
    void getCategoryById_ValidId_ShouldReturnResponse() {
        // Arrange
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        // Act
        CategoryResponse response = categoryService.getCategoryById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(categoryRepository).findById(1L);
    }
}
