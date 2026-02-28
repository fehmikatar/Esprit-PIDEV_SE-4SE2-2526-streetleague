package tn.esprit._4se2.pi.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit._4se2.pi.dto.CategoryDTO;
import tn.esprit._4se2.pi.repository.CategoryRepository;
import tn.esprit._4se2.pi.entities.Category;
import tn.esprit._4se2.pi.mappers.CategoryMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements ICategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;


    @Override
    public CategoryDTO addCategory(CategoryDTO categoryDTO) {
        if (categoryRepository.existsByNom(categoryDTO.getNom())) {
            return null;
        }

        Category category = categoryMapper.toEntity(categoryDTO);

        if (categoryDTO.getParentId() != null) {
            Category parent = categoryRepository.findById(categoryDTO.getParentId()).orElse(null);
            category.setParentCategory(parent);
        }

        category.setCreatedAt(LocalDateTime.now());

        Category saved = categoryRepository.save(category);
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
                .createdAt(LocalDateTime.now())
                .build();

        Category category = categoryRepository.findById(id).orElse(defaultCategory);
        return categoryMapper.toDTO(category);
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryDTO> getRootCategories() {
        return categoryRepository.findByParentCategoryIsNull().stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDTO getCategoryWithSubCategories(Long id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toDTOWithSubCategories)
                .orElse(null);
    }

    @Override
    public void deleteCategoryById(Long id) {
        categoryRepository.findById(id).ifPresent(category -> {
            if (category.getProducts().isEmpty()) {
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
    }
}
