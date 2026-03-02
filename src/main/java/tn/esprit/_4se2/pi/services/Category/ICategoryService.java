package tn.esprit._4se2.pi.services.Category;

import tn.esprit._4se2.pi.entities.Category;

import java.util.List;

public interface ICategoryService {
    Category addCategory(Category category);

    List<Category> getAllCategories();

    Category getCategoryById(Long id);

    void deleteCategory(Long id);
}
