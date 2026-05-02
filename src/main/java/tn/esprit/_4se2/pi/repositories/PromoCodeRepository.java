package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit._4se2.pi.entities.PromoCode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {

    Optional<PromoCode> findByCode(String code);

    List<PromoCode> findByActiveTrue();

    @Query("SELECT p FROM PromoCode p WHERE p.expiryDate < :date AND p.active = true")
    List<PromoCode> findExpiredActivePromoCodes(@Param("date") LocalDateTime date);

    @Query("""
        SELECT p.code, p.timesUsed, COALESCE(SUM(c.total), 0)
        FROM Cart c JOIN c.appliedPromoCode p
        GROUP BY p.code, p.timesUsed
        ORDER BY COALESCE(SUM(c.total), 0) DESC
    """)
    List<Object[]> findPromoCodeRevenueStats();

    boolean existsByCode(String code);
}
