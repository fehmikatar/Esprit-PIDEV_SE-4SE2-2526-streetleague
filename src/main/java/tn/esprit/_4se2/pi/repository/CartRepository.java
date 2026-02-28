package tn.esprit._4se2.pi.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.entities.Cart;
import tn.esprit._4se2.pi.entities.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUser(User user);

    Optional<Cart> findByUserId(Long userId);

    @Query("SELECT c FROM Cart c WHERE c.status = 'ABANDONED' AND c.lastModified < :threshold")
    List<Cart> findAbandonedCarts(@Param("threshold") LocalDateTime threshold);

    @Modifying
    @Query("UPDATE Cart c SET c.status = 'ABANDONED' WHERE c.status = 'ACTIVE' AND c.lastModified < :threshold")
    int markCartsAsAbandoned(@Param("threshold") LocalDateTime threshold);

    @Query("SELECT AVG(c.total) FROM Cart c WHERE c.status = 'CONVERTED'")
    Double findAverageCartValue();

    @Query("SELECT COUNT(c) FROM Cart c WHERE c.status = 'ABANDONED'")
    long countAbandonedCarts();
}
