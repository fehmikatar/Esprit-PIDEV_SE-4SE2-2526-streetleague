package tn.esprit._4se2.pi.services.Category;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit._4se2.pi.dto.Sponsor.CategoryDTO;
import tn.esprit._4se2.pi.entities.Category;
import tn.esprit._4se2.pi.mappers.CategoryMapper;
import tn.esprit._4se2.pi.repositories.CategoryRepository;
import tn.esprit._4se2.pi.services.Community.CommunityService;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private CommunityService communityService;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void addCategoryReturnsNullWhenNameAlreadyExists() {
        CategoryDTO dto = CategoryDTO.builder().nom("Football").description("desc").build();
        when(categoryRepository.existsByNom("Football")).thenReturn(true);

        CategoryDTO result = categoryService.addCategory(dto);

        assertNull(result);
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void addCategoryWithParentSetsParentAndEnsuresCommunity() {
        CategoryDTO dto = CategoryDTO.builder()
                .nom("Football")
                .description("desc")
                .parentId(7L)
                .build();

        Category categoryToSave = Category.builder().nom("Football").description("desc").build();
        Category parent = Category.builder().id(7L).nom("Sports").build();
        Category saved = Category.builder().id(10L).nom("Football").description("desc").parentCategory(parent).build();
        CategoryDTO expected = CategoryDTO.builder().id(10L).nom("Football").description("desc").parentId(7L).build();

        when(categoryRepository.existsByNom("Football")).thenReturn(false);
        when(categoryMapper.toEntity(dto)).thenReturn(categoryToSave);
        when(categoryRepository.findById(7L)).thenReturn(Optional.of(parent));
        when(categoryRepository.save(categoryToSave)).thenReturn(saved);
        when(categoryMapper.toDTO(saved)).thenReturn(expected);

        CategoryDTO result = categoryService.addCategory(dto);

        assertSame(expected, result);
        assertSame(parent, categoryToSave.getParentCategory());
        assertNotNull(categoryToSave.getCreatedAt());
        verify(communityService).ensureCommunityForCategory(saved);
    }

    @Test
    void updateCategoryReturnsNullWhenNewNameAlreadyExists() {
        Long categoryId = 1L;
        Category existing = Category.builder().id(categoryId).nom("Old").description("old").build();
        CategoryDTO dto = CategoryDTO.builder().nom("New").description("new").build();

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(existing));
        when(categoryRepository.existsByNom("New")).thenReturn(true);

        CategoryDTO result = categoryService.updateCategory(categoryId, dto);

        assertNull(result);
        verify(categoryRepository, never()).save(any(Category.class));
        verify(communityService, never()).syncCommunityWithCategory(any(Category.class));
    }

    @Test
    void deleteCategoryByIdDeletesCategoryWithoutProducts() {
        Long categoryId = 5L;
        Category category = Category.builder().id(categoryId).nom("NoProduct").products(new ArrayList<>()).build();
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        categoryService.deleteCategoryById(categoryId);

        verify(communityService).deleteByCategoryId(categoryId);
        verify(categoryRepository).delete(category);
    }
}