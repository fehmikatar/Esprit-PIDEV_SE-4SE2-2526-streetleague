package tn.esprit._4se2.pi.repositories;

import tn.esprit._4se2.pi.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.dto.Sponsor.ProductDTOs;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByIdAndDeletedFalse(Long id);

    @Query("SELECT p FROM Product p " +
           "LEFT JOIN FETCH p.category " +
           "LEFT JOIN FETCH p.variants " +
           "WHERE p.id = :id AND p.deleted = false")
    Optional<Product> findByIdWithDetails(@Param("id") Long id);

    Page<Product> findByDeletedFalse(Pageable pageable);

    boolean existsByNomAndDeletedFalse(String nom);

    List<Product> findByDeletedFalse();

    List<Product> findByCategoryIdAndDeletedFalse(Long categoryId);

    List<Product> findByStockLessThanAndDeletedFalse(Integer threshold);

    List<Product> findByPrixBetweenAndDeletedFalse(BigDecimal min, BigDecimal max);

    // ✅ Version CORRECTE - Utilise une sous-requête
    @Query("SELECT p FROM Product p " +
            "WHERE p.deleted = false " +
            "ORDER BY (SELECT COUNT(f) FROM Favorite f WHERE f.product = p) DESC")
    List<Product> findMostFavoritedProducts();

    // ✅ Version avec Pageable
    @Query("SELECT p FROM Product p " +
            "WHERE p.deleted = false " +
            "ORDER BY (SELECT COUNT(f) FROM Favorite f WHERE f.product = p) DESC")
    List<Product> findMostFavoritedProducts(Pageable pageable);

    @Query("SELECT p FROM Product p " +
            "WHERE p.deleted = false " +
            "  AND p.id NOT IN (" +
            "       SELECT ci.product.id " +
            "       FROM CartItem ci " +
            "       JOIN ci.cart c " +
            "       WHERE c.status = 'CONVERTED'" +
            "  )")
    List<Product> findProductsNeverOrdered();

    @Query("SELECT p FROM Product p " +
            "LEFT JOIN p.category cat " +
            "WHERE p.deleted = false " +
            "  AND (:keyword IS NULL OR LOWER(p.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "       OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "  AND (:categoryId IS NULL OR cat.id = :categoryId) " +
            "  AND (:minPrice IS NULL OR p.prix >= :minPrice) " +
            "  AND (:maxPrice IS NULL OR p.prix <= :maxPrice) " +
            "ORDER BY p.createdAt DESC")
    Page<Product> searchProducts(@Param("keyword") String keyword,
                                 @Param("categoryId") Long categoryId,
                                 @Param("minPrice") BigDecimal minPrice,
                                 @Param("maxPrice") BigDecimal maxPrice,
                                 Pageable pageable);

    @Query("SELECT new tn.esprit._4se2.pi.dto.Sponsor.ProductDTOs$ProductHighDemandDTO(" +
           "p.id, p.nom, SUM(ci.quantity), p.stock, cat.nom) " +
           "FROM Product p " +
           "JOIN p.category cat " +
           "JOIN CartItem ci ON ci.product = p " +
           "JOIN ci.cart c " +
           "WHERE c.status = 'ACTIVE' " +
           "GROUP BY p.id, p.nom, p.stock, cat.nom " +
           "ORDER BY SUM(ci.quantity) DESC")
    List<ProductDTOs.ProductHighDemandDTO> findHighDemandProducts();
}