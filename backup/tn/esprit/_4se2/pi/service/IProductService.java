package tn.esprit._4se2.pi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit._4se2.pi.dto.ProductDTOs;

import java.io.IOException;
import java.util.List;

public interface IProductService {
    ProductDTOs.ProductResponse addProduct(ProductDTOs.ProductRequest request);
    List<ProductDTOs.ProductResponse> addProducts(List<ProductDTOs.ProductRequest> requests);

    ProductDTOs.ProductResponse updateProduct(Long id, ProductDTOs.ProductRequest request);

    ProductDTOs.ProductResponse getProductById(Long id);
    ProductDTOs.ProductResponse getProductByIdOrElse(Long id);
    Page<ProductDTOs.ProductResponse> getAllProducts(Pageable pageable);
    Page<ProductDTOs.ProductResponse> searchProducts(ProductDTOs.ProductSearchCriteria criteria, Pageable pageable);

    void deleteProductById(Long id);
    void deleteAllProducts();

    long countProducts();
    boolean existsById(Long id);

    void updateStock(Long productId, int quantity);
    List<ProductDTOs.ProductResponse> getLowStockProducts(int threshold);
    List<ProductDTOs.ProductResponse> getMostFavoritedProducts(int limit);

    List<ProductDTOs.ProductResponse> bulkImportProducts(MultipartFile file) throws IOException;
    byte[] bulkExportProducts() throws IOException;
}