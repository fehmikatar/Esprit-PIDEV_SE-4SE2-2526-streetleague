package tn.esprit._4se2.pi.services.Category;

import tn.esprit._4se2.pi.dto.CategoryRequest;
import tn.esprit._4se2.pi.dto.CategoryResponse;

import java.util.List;

public interface ICategoryService {
    CategoryResponse addCategory(CategoryRequest request);

    List<CategoryResponse> getAllCategories();

<<<<<<< Updated upstream
    CategoryResponse getCategoryById(Long id);
=======
    CategoryDTO getCategoryById(Long id);
    CategoryDTO getCategoryByIdOrElse(Long id);
    List<CategoryDTO> getAllCategories();
    List<CategoryDTO> getRootCategories();
    CategoryDTO getCategoryWithSubCategories(Long id);
    List<CategoryDTO> searchByKeywordAcrossCategoryAndProducts(String keyword);
>>>>>>> Stashed changes

    CategoryResponse updateCategory(Long id, CategoryRequest request);

    void deleteCategory(Long id);
}
