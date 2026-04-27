package tn.esprit._4se2.pi.repositories;

import tn.esprit._4se2.pi.entities.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
    List<ProductVariant> findByProductId(Long productId);
    Optional<ProductVariant> findBySku(String sku);

    List<ProductVariant> findByStockLessThanAndProductDeletedFalse(Integer threshold);
/*
    @Query("SELECT p.nom, v.size, v.color, v.stock " +
           "FROM ProductVariant v " +
           "JOIN v.product p " +
           "WHERE p.deleted = false AND v.stock < :threshold")
    List<Object[]> findLowStockVariantsWithProductName(@Param("threshold") Integer threshold);

 */
}
