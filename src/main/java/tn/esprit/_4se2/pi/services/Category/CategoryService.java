package tn.esprit._4se2.pi.services.Category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit._4se2.pi.dto.CategoryRequest;
import tn.esprit._4se2.pi.dto.CategoryResponse;
import tn.esprit._4se2.pi.entities.Category;
import tn.esprit._4se2.pi.repositories.CategoryRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    public CategoryResponse addCategory(CategoryRequest request) {
        Category category = Category.builder()
                .nom(request.getName().trim())
                .description(request.getDescription())
                .capacity(request.getCapacity())
                .createdAt(LocalDateTime.now())
                .build();

        return toResponse(categoryRepository.save(category));
    }

    @Override
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryResponse getCategoryById(Long id) {
        return toResponse(findCategoryOrThrow(id));
    }

    @Override
    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = findCategoryOrThrow(id);
        category.setNom(request.getName().trim());
        category.setDescription(request.getDescription());
        category.setCapacity(request.getCapacity());

        return toResponse(categoryRepository.save(category));
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = findCategoryOrThrow(id);
        categoryRepository.delete(category);
    }

    private Category findCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }

    private CategoryResponse toResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getNom());
        response.setDescription(category.getDescription());
        response.setCapacity(category.getCapacity());
        return response;
    }
}
