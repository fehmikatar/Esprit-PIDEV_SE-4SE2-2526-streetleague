package tn.esprit._4se2.pi.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit._4se2.pi.dto.ProductDTOs;
import tn.esprit._4se2.pi.repository.ProductRepository;
import tn.esprit._4se2.pi.repository.CategoryRepository;
import tn.esprit._4se2.pi.entities.Category;
import tn.esprit._4se2.pi.entities.Product;
import tn.esprit._4se2.pi.entities.ProductVariant;
import tn.esprit._4se2.pi.mappers.ProductMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ProductServiceImpl implements IProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductDTOs.ProductResponse addProduct(ProductDTOs.ProductRequest request) {
        // Vérifier si un produit avec le même nom existe déjà
        if (productRepository.existsByNomAndDeletedFalse(request.getNom())) {
            return null;
        }

        Product product = productMapper.toEntity(request);

        // Assigner la catégorie si fournie
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId()).orElse(null);
            product.setCategory(category);
        }

        // Initialiser les dates
        LocalDateTime now = LocalDateTime.now();
        product.setCreatedAt(now);
        product.setUpdatedAt(now);
        product.setDeleted(false);

        // Ajouter les variantes si existantes
        if (request.getVariants() != null && !request.getVariants().isEmpty()) {
            request.getVariants().forEach(variantDTO -> {
                ProductVariant variant = productMapper.toVariantEntity(variantDTO, product);
                variant.setCreatedAt(now);
                product.getVariants().add(variant);
            });
        }

        Product saved = productRepository.save(product);
        return productMapper.toDTO(saved);
    }

    @Override
    public List<ProductDTOs.ProductResponse> addProducts(List<ProductDTOs.ProductRequest> requests) {
        return requests.stream()
                .map(this::addProduct)
                .filter(response -> response != null)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTOs.ProductResponse updateProduct(Long id, ProductDTOs.ProductRequest request) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isEmpty() || optionalProduct.get().getDeleted()) {
            return null;
        }

        Product product = optionalProduct.get();
        product.setNom(request.getNom());
        product.setDescription(request.getDescription());
        product.setPrix(request.getPrix());
        product.setStock(request.getStock());
        product.setImages(request.getImages());

        // Mettre à jour la catégorie
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findById(request.getCategoryId()).orElse(null);
            product.setCategory(category);
        } else {
            product.setCategory(null);
        }

        // Mettre à jour la date
        product.setUpdatedAt(LocalDateTime.now());

        // Mettre à jour les variantes
        if (request.getVariants() != null) {
            product.getVariants().clear();
            LocalDateTime now = LocalDateTime.now();
            request.getVariants().forEach(variantDTO -> {
                ProductVariant variant = productMapper.toVariantEntity(variantDTO, product);
                variant.setCreatedAt(now);
                product.getVariants().add(variant);
            });
        }

        Product updated = productRepository.save(product);
        return productMapper.toDTO(updated);
    }

    @Override
    public ProductDTOs.ProductResponse getProductById(Long id) {
        return productRepository.findByIdAndDeletedFalse(id)
                .map(productMapper::toDTO)
                .orElse(null);
    }

    @Override
    public ProductDTOs.ProductResponse getProductByIdOrElse(Long id) {
        Product defaultProduct = Product.builder()
                .id(0L)
                .nom("Product Not Found")
                .description("Default product when not found")
                .prix(BigDecimal.ZERO)
                .stock(0)
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Product product = productRepository.findById(id).orElse(defaultProduct);
        return productMapper.toDTO(product);
    }

    @Override
    public Page<ProductDTOs.ProductResponse> getAllProducts(Pageable pageable) {
        return productRepository.findByDeletedFalse(pageable)
                .map(productMapper::toDTO);
    }

    @Override
    public Page<ProductDTOs.ProductResponse> searchProducts(ProductDTOs.ProductSearchCriteria criteria, Pageable pageable) {
        return productRepository.searchProducts(
                criteria.getKeyword(),
                criteria.getCategoryId(),
                criteria.getMinPrice(),
                criteria.getMaxPrice(),
                pageable
        ).map(productMapper::toDTO);
    }

    @Override
    public void deleteProductById(Long id) {
        productRepository.findById(id).ifPresent(product -> {
            product.setDeleted(true);
            product.setUpdatedAt(LocalDateTime.now());
            productRepository.save(product);
        });
    }

    @Override
    public void deleteAllProducts() {
        List<Product> products = productRepository.findAll();
        products.forEach(product -> {
            product.setDeleted(true);
            product.setUpdatedAt(LocalDateTime.now());
        });
        productRepository.saveAll(products);
    }

    @Override
    public long countProducts() {
        return productRepository.count();
    }

    @Override
    public boolean existsById(Long id) {
        return productRepository.existsById(id);
    }

    @Override
    public void updateStock(Long productId, int quantity) {
        productRepository.findById(productId).ifPresent(product -> {
            if (!product.getDeleted() && product.getStock() >= quantity) {
                product.setStock(product.getStock() - quantity);
                product.setUpdatedAt(LocalDateTime.now());
                productRepository.save(product);
            }
        });
    }

    @Override
    public List<ProductDTOs.ProductResponse> getLowStockProducts(int threshold) {
        return productRepository.findByStockLessThanAndDeletedFalse(threshold).stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTOs.ProductResponse> getMostFavoritedProducts(int limit) {
        return productRepository.findMostFavoritedProducts(Pageable.ofSize(limit)).stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTOs.ProductResponse> bulkImportProducts(MultipartFile file) throws IOException {
        // TODO: Implémenter l'import CSV
        // Cette méthode sera implémentée plus tard
        return List.of();
    }

    @Override
    public byte[] bulkExportProducts() throws IOException {
        // TODO: Implémenter l'export CSV
        // Cette méthode sera implémentée plus tard
        return new byte[0];
    }
}