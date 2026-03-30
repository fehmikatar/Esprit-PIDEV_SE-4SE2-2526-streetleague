package tn.esprit._4se2.pi.repository;

import tn.esprit._4se2.pi.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByIdAndDeletedFalse(Long id);

    Page<Product> findByDeletedFalse(Pageable pageable);

    boolean existsByNomAndDeletedFalse(String nom);

    @Query("SELECT p FROM Product p WHERE p.deleted = false AND " +
            "(:keyword IS NULL OR LOWER(p.nom) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
            "(:minPrice IS NULL OR p.prix >= :minPrice) AND " +
            "(:maxPrice IS NULL OR p.prix <= :maxPrice)")
    Page<Product> searchProducts(@Param("keyword") String keyword,
                                 @Param("categoryId") Long categoryId,
                                 @Param("minPrice") BigDecimal minPrice,
                                 @Param("maxPrice") BigDecimal maxPrice,
                                 Pageable pageable);

    List<Product> findByStockLessThanAndDeletedFalse(Integer threshold);

    // CORRECTION: Retirez la référence à 'favorites' si la relation n'existe pas
    // Ou commentez cette méthode temporairement
    // @Query("SELECT p FROM Product p WHERE p.deleted = false ORDER BY SIZE(p.favorites) DESC")
    // List<Product> findMostFavoritedProducts(Pageable pageable);

    // Version alternative sans la relation favorites
    @Query("SELECT p FROM Product p WHERE p.deleted = false ORDER BY p.id DESC")
    List<Product> findMostFavoritedProducts(Pageable pageable);
}