package tn.esprit._4se2.pi.services.Category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.dto.categorie.CategoryRequest;
import tn.esprit._4se2.pi.dto.categorie.CategoryResponse;
import tn.esprit._4se2.pi.entities.Category;
import tn.esprit._4se2.pi.entities.SportCommunity;
import tn.esprit._4se2.pi.exception.ConflictException;
import tn.esprit._4se2.pi.repositories.CategoryRepository;
import tn.esprit._4se2.pi.repositories.SportCommunityRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final SportCommunityRepository sportCommunityRepository;

    @Override
    @Transactional
    public CategoryResponse addCategory(CategoryRequest request) {
        String normalizedName = request.getName().trim();
        categoryRepository.findByNomIgnoreCase(normalizedName)
                .ifPresent(existing -> {
                    throw new ConflictException("Category already exists: " + normalizedName);
                });

        Category category = Category.builder()
                .nom(normalizedName)
                .description(request.getDescription())
                .capacity(request.getCapacity())
                .createdAt(LocalDateTime.now())
                .build();

        Category savedCategory = categoryRepository.save(category);

        sportCommunityRepository.findBySportCategoryId(savedCategory.getId())
                .orElseGet(() -> sportCommunityRepository.save(
                        SportCommunity.builder()
                                .name(savedCategory.getNom() + " Community")
                                .sportCategory(savedCategory)
                                .createdAt(LocalDateTime.now())
                                .build()
                ));

        return toResponse(savedCategory);
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

        String normalizedName = request.getName().trim();
        categoryRepository.findByNomIgnoreCase(normalizedName)
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new ConflictException("Category already exists: " + normalizedName);
                });

        category.setNom(normalizedName);
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