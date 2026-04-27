package tn.esprit._4se2.pi.services.Category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.dto.CategoryRequest;
import tn.esprit._4se2.pi.dto.CategoryResponse;
import tn.esprit._4se2.pi.entities.Category;
<<<<<<< Updated upstream
import tn.esprit._4se2.pi.entities.SportCommunity;
import tn.esprit._4se2.pi.exception.ConflictException;
import tn.esprit._4se2.pi.repositories.CategoryRepository;
import tn.esprit._4se2.pi.repositories.SportCommunityRepository;
=======
import tn.esprit._4se2.pi.mappers.CategoryMapper;
import tn.esprit._4se2.pi.services.Community.CommunityService;
>>>>>>> Stashed changes

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;
<<<<<<< Updated upstream
    private final SportCommunityRepository sportCommunityRepository;
=======
    private final CategoryMapper categoryMapper;
    private final CommunityService communityService;

>>>>>>> Stashed changes

    @Override
    @Transactional
    public CategoryResponse addCategory(CategoryRequest request) {
        String normalizedName = request.getName().trim();
        categoryRepository.findByNomIgnoreCase(normalizedName)
                .ifPresent(existing -> {
                    throw new ConflictException("Category already exists: " + normalizedName);
                });

<<<<<<< Updated upstream
        Category category = Category.builder()
                .nom(normalizedName)
                .description(request.getDescription())
                .capacity(request.getCapacity())
=======
        Category category = categoryMapper.toEntity(categoryDTO);

        if (categoryDTO.getParentId() != null) {
            Category parent = categoryRepository.findById(categoryDTO.getParentId()).orElse(null);
            category.setParentCategory(parent);
        }

        category.setCreatedAt(LocalDateTime.now());

        Category saved = categoryRepository.save(category);
        communityService.ensureCommunityForCategory(saved);
        return categoryMapper.toDTO(saved);
    }

    @Override
    public List<CategoryDTO> addCategories(List<CategoryDTO> categoryDTOs) {
        return categoryDTOs.stream()
                .map(this::addCategory)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if (optionalCategory.isEmpty()) {
            return null;
        }

        Category category = optionalCategory.get();

        if (!category.getNom().equals(categoryDTO.getNom()) &&
                categoryRepository.existsByNom(categoryDTO.getNom())) {
            return null;
        }

        category.setNom(categoryDTO.getNom());
        category.setDescription(categoryDTO.getDescription());

        if (categoryDTO.getParentId() != null && !categoryDTO.getParentId().equals(id)) {
            Category parent = categoryRepository.findById(categoryDTO.getParentId()).orElse(null);
            category.setParentCategory(parent);
        } else if (categoryDTO.getParentId() == null) {
            category.setParentCategory(null);
        }

        Category updated = categoryRepository.save(category);
        communityService.syncCommunityWithCategory(updated);
        return categoryMapper.toDTO(updated);
    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toDTO)
                .orElse(null);
    }

    @Override
    public CategoryDTO getCategoryByIdOrElse(Long id) {
        Category defaultCategory = Category.builder()
                .id(0L)
                .nom("Category Not Found")
                .description("Default category when not found")
>>>>>>> Stashed changes
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

<<<<<<< Updated upstream
    private CategoryResponse toResponse(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getNom());
        response.setDescription(category.getDescription());
        response.setCapacity(category.getCapacity());
        return response;
=======
    @Override
    public List<CategoryDTO> searchByKeywordAcrossCategoryAndProducts(String keyword) {
        String safeKeyword = keyword == null ? "" : keyword.trim();
        return categoryRepository
                .findDistinctByNomContainingIgnoreCaseOrProducts_NomContainingIgnoreCase(safeKeyword, safeKeyword)
                .stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteCategoryById(Long id) {
        categoryRepository.findById(id).ifPresent(category -> {
            if (category.getProducts().isEmpty()) {
                communityService.deleteByCategoryId(category.getId());
                categoryRepository.delete(category);
            }
        });
    }

    @Override
    public void deleteAllCategories() {
        categoryRepository.deleteAll();
    }

    @Override
    public long countCategories() {
        return categoryRepository.count();
    }

    @Override
    public boolean existsById(Long id) {
        return categoryRepository.existsById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return categoryRepository.existsByNom(name);
>>>>>>> Stashed changes
    }
}
