package tn.esprit._4se2.pi.repositories;

import tn.esprit._4se2.pi.entities.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    // Méthodes de requête personnalisées (optionnel)
    List<Promotion> findByEndDateAfter(LocalDate now);
    Promotion findByPromoCode(String promoCode);
}