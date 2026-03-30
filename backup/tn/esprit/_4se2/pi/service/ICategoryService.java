package tn.esprit._4se2.pi.service;

import tn.esprit._4se2.pi.dto.CategoryDTO;

import java.util.List;

public interface ICategoryService {
    CategoryDTO addCategory(CategoryDTO categoryDTO);
    List<CategoryDTO> addCategories(List<CategoryDTO> categoryDTOs);

    CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO);

    CategoryDTO getCategoryById(Long id);
    CategoryDTO getCategoryByIdOrElse(Long id);
    List<CategoryDTO> getAllCategories();
    List<CategoryDTO> getRootCategories();
    CategoryDTO getCategoryWithSubCategories(Long id);

    void deleteCategoryById(Long id);
    void deleteAllCategories();

    long countCategories();
    boolean existsById(Long id);
    boolean existsByName(String name);
}
