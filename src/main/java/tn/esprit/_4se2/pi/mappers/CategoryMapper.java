package tn.esprit._4se2.pi.mappers;

import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.dto.CategoryDTO;
import tn.esprit._4se2.pi.entities.Category;

import java.util.stream.Collectors;

@Component
public class CategoryMapper {

    public Category toEntity(CategoryDTO dto) {
        if (dto == null) return null;

        return Category.builder()
                .nom(dto.getNom())
                .description(dto.getDescription())
                .build();
    }

    public CategoryDTO toDTO(Category category) {
        if (category == null) return null;

        return CategoryDTO.builder()
                .id(category.getId())
                .nom(category.getNom())
                .description(category.getDescription())
                .parentId(category.getParentCategory() != null ? category.getParentCategory().getId() : null)
                .createdAt(category.getCreatedAt())
                .build();
    }

    public CategoryDTO toDTOWithSubCategories(Category category) {
        CategoryDTO dto = toDTO(category);
        if (dto != null && category.getSubCategories() != null) {
            dto.setSubCategories(category.getSubCategories().stream()
                    .map(this::toDTO)
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}
