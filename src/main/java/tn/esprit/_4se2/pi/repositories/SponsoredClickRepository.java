package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.entities.SponsoredClick;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SponsoredClickRepository extends JpaRepository<SponsoredClick, Long> {

    List<SponsoredClick> findByUserId(Long userId);

    List<SponsoredClick> findByProductId(Long productId);

    List<SponsoredClick> findByClickedAtAfter(LocalDateTime since);

    @Query("SELECT sc.product.id, COUNT(sc) FROM SponsoredClick sc WHERE sc.isClicked = true GROUP BY sc.product.id")
    List<Object[]> getTopClickedProducts();

    @Query("SELECT sc.product.id, COUNT(sc) FROM SponsoredClick sc WHERE sc.isPurchased = true GROUP BY sc.product.id")
    List<Object[]> getTopPurchasedProducts();

    @Query("SELECT AVG(CASE WHEN sc.isClicked = true THEN 1.0 ELSE 0.0 END) FROM SponsoredClick sc")
    Double getGlobalCTR();

    @Query("SELECT sc.sponsoredPosition, COUNT(sc), SUM(CASE WHEN sc.isClicked = true THEN 1 ELSE 0 END) " +
            "FROM SponsoredClick sc WHERE sc.sponsoredPosition IS NOT NULL GROUP BY sc.sponsoredPosition")
    List<Object[]> getCTRByPosition();

    long countByClickedAtAfter(LocalDateTime since);
}