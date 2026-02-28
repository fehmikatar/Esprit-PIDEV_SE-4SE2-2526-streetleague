package tn.esprit._4se2.pi.restcontrollers;

import tn.esprit._4se2.pi.dto.ProductDTOs;
import tn.esprit._4se2.pi.service.IProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Product Management", description = "Endpoints for managing products")
public class ProductRestController {

    private final IProductService productService;

    @GetMapping
    @Operation(summary = "Get all products", description = "Returns a paginated list of all products")
    public ResponseEntity<Page<ProductDTOs.ProductResponse>> getAllProducts(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(productService.getAllProducts(pageable));
    }

    @GetMapping("/search")
    @Operation(summary = "Search products", description = "Search products with filters")
    public ResponseEntity<Page<ProductDTOs.ProductResponse>> searchProducts(
            @ModelAttribute ProductDTOs.ProductSearchCriteria criteria,
            Pageable pageable) {
        return ResponseEntity.ok(productService.searchProducts(criteria, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Returns a single product")
    public ResponseEntity<ProductDTOs.ProductResponse> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Get low stock products", description = "Returns products with stock below threshold")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProductDTOs.ProductResponse>> getLowStockProducts(
            @RequestParam(defaultValue = "10") int threshold) {
        return ResponseEntity.ok(productService.getLowStockProducts(threshold));
    }

    @GetMapping("/most-favorited")
    @Operation(summary = "Get most favorited products", description = "Returns the most favorited products")
    public ResponseEntity<List<ProductDTOs.ProductResponse>> getMostFavoritedProducts(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(productService.getMostFavoritedProducts(limit));
    }

    @PostMapping
    @Operation(summary = "Create product", description = "Creates a new product")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTOs.ProductResponse> createProduct(
            @Valid @RequestBody ProductDTOs.ProductRequest request) {
        return new ResponseEntity<>(productService.addProduct(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update product", description = "Updates an existing product")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductDTOs.ProductResponse> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductDTOs.ProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product", description = "Soft deletes a product")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/bulk/import")
    @Operation(summary = "Bulk import products", description = "Import products from CSV file")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> bulkImport(@RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(productService.bulkImportProducts(file));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error importing products: " + e.getMessage());
        }
    }

    @GetMapping("/bulk/export")
    @Operation(summary = "Bulk export products", description = "Export products to CSV file")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> bulkExport() {
        try {
            byte[] csvData = productService.bulkExportProducts();
            return ResponseEntity.ok()
                    .header("Content-Type", "text/csv")
                    .header("Content-Disposition", "attachment; filename=products.csv")
                    .body(csvData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}