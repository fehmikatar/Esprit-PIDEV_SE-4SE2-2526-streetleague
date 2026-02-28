package tn.esprit._4se2.pi.repository;
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
}
