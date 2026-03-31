package tn.esprit._4se2.pi.mappers;

import tn.esprit._4se2.pi.dto.CategoryRequest;
import tn.esprit._4se2.pi.dto.CategoryResponse;
import tn.esprit._4se2.pi.entities.Category;

public class CategoryMapper {
    public static Category toEntity(CategoryRequest dto) {
        Category category = new Category();
        category.setNom(dto.getName());
        category.setDescription(dto.getDescription());
        return category;
    }

    public static CategoryResponse toDto(Category category) {
        CategoryResponse dto = new CategoryResponse();
        dto.setId(category.getId());
        dto.setName(category.getNom());
        dto.setDescription(category.getDescription());
        return dto;
    }
}
