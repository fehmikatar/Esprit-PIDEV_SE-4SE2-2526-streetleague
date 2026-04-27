package tn.esprit._4se2.pi.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.entities.PromoCode;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromoCodeRepository extends JpaRepository<PromoCode, Long> {

    Optional<PromoCode> findByCode(String code);

    List<PromoCode> findByActiveTrue();

    @Query("SELECT p FROM PromoCode p WHERE p.expiryDate < :date AND p.active = true")
    List<PromoCode> findExpiredActivePromoCodes(@Param("date") LocalDateTime date);

    boolean existsByCode(String code);

    // ─── MOTS-CLÉS ──────────────────────────────────────────────────────────

    /**
     * KEYWORD — Utilisé par le SCHEDULER (tâche 2).
     * Trouve les promos actives ayant dépassé la date d'expiration.
     */
    List<PromoCode> findByActiveTrueAndExpiryDateBefore(LocalDateTime now);

    /**
     * KEYWORD — Trouve les promos actives par type.
     */
    List<PromoCode> findByActiveTrueAndDiscountType(PromoCode.DiscountType type);


    // ─── JPQL AVEC JOINTURES ─────────────────────────────────────────────────

    /**
     * JPQL avec jointures — Chiffre d'affaires généré par code promo.
     */
    @Query("SELECT pc.code, COUNT(c.id), SUM(c.total) " +
           "FROM Cart c " +
           "JOIN c.appliedPromoCode pc " +           // JOIN Cart → PromoCode
           "WHERE c.status = 'CONVERTED' " +
           "GROUP BY pc.code")
    List<Object[]> findPromoCodeRevenueStats();

}
