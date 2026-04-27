package tn.esprit._4se2.pi.restcontrollers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.dto.CategoryRequest;
import tn.esprit._4se2.pi.dto.CategoryResponse;
import tn.esprit._4se2.pi.services.Category.ICategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryRestController {

    private final ICategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> addCategory(@Valid @RequestBody CategoryRequest category) {
        return ResponseEntity.ok(categoryService.addCategory(category));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

<<<<<<< Updated upstream
=======
    @GetMapping("/{id}/with-subcategories")
    public ResponseEntity<CategoryDTO> getCategoryWithSubCategories(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryWithSubCategories(id));
    }

    @GetMapping("/search/keyword")
    public ResponseEntity<List<CategoryDTO>> searchCategoryByKeyword(@RequestParam(defaultValue = "") String keyword) {
        return ResponseEntity.ok(categoryService.searchByKeywordAcrossCategoryAndProducts(keyword));
    }

    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        return new ResponseEntity<>(categoryService.addCategory(categoryDTO), HttpStatus.CREATED);
    }

>>>>>>> Stashed changes
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest category) {
        return ResponseEntity.ok(categoryService.updateCategory(id, category));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
