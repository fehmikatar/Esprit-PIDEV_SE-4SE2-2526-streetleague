package tn.esprit._4se2.pi.services.Category;

import tn.esprit._4se2.pi.dto.CategoryRequest;
import tn.esprit._4se2.pi.dto.CategoryResponse;

import java.util.List;

public interface ICategoryService {
    CategoryResponse addCategory(CategoryRequest request);

    List<CategoryResponse> getAllCategories();

    CategoryResponse getCategoryById(Long id);

    CategoryResponse updateCategory(Long id, CategoryRequest request);

    void deleteCategory(Long id);
}
