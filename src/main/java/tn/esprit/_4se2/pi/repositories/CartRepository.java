package tn.esprit._4se2.pi.repositories;
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


    List<Cart> findByStatusAndLastModifiedBefore(Cart.CartStatus status, LocalDateTime cutoff);


    Optional<Cart> findByUserIdAndStatus(Long userId, Cart.CartStatus status);


    List<Cart> findByUserIdOrderByCreatedAtDesc(Long userId);


    Optional<Cart> findByOrderCode(String orderCode);


    List<Cart> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, Cart.CartStatus status);


    @Query("SELECT p.id, p.nom, p.category.nom, " +
           "       SUM(ci.quantity) AS totalQty, " +
           "       SUM(ci.price * ci.quantity) AS totalRevenue " +
           "FROM Cart c " +
           "JOIN c.items ci " +               
           "JOIN ci.product p " +             
           "JOIN p.category cat " +           
           "WHERE c.status = 'CONVERTED' " +
           "GROUP BY p.id, p.nom, p.category.nom " +
           "ORDER BY totalQty DESC")
    List<Object[]> findTopSellingProductsWithCategory();


    @Query("SELECT c.orderCode, c.total, c.createdAt, " +
           "       COUNT(ci.id) AS itemCount, c.deliveryStatus " +
           "FROM Cart c " +
           "JOIN c.user u " +                 
           "JOIN c.items ci " +               
           "WHERE u.id = :userId " +
           "  AND c.status = 'CONVERTED' " +
           "GROUP BY c.id, c.orderCode, c.total, c.createdAt, c.deliveryStatus " +
           "ORDER BY c.createdAt DESC")
    List<Object[]> findOrderSummaryByUserId(@Param("userId") Long userId);


    @Query("SELECT DISTINCT c " +
           "FROM Cart c " +
           "JOIN c.items ci " +               
           "JOIN ci.product p " +             
           "WHERE p.id = :productId " +
           "  AND c.status = 'ACTIVE'")
    List<Cart> findActiveCartsContainingProduct(@Param("productId") Long productId);


    @Query("SELECT c.clientCity, " +
           "       COUNT(c.id) AS cartCount, " +
           "       SUM(c.total) AS lostRevenue " +
           "FROM Cart c " +
           "WHERE c.status = 'ABANDONED' " +
           "  AND c.clientCity IS NOT NULL " +
           "GROUP BY c.clientCity " +
           "ORDER BY lostRevenue DESC")
    List<Object[]> findAbandonedCartStatsByCity();

    @Query("SELECT AVG(c.total) FROM Cart c WHERE c.status = 'CONVERTED'")
    Double findAverageCartValue();

    @Query("SELECT COUNT(c) FROM Cart c WHERE c.status = 'ABANDONED'")
    long countAbandonedCarts();

    // Garder les méthodes existantes si nécessaire
    List<Cart> findByUserIdAndStatusOrderByLastModifiedDesc(Long userId, Cart.CartStatus status);
    List<Cart> findByStatus(Cart.CartStatus status);
    List<Cart> findByStatusOrderByLastModifiedDesc(Cart.CartStatus status);
    @Query("SELECT c FROM Cart c WHERE c.status = 'ABANDONED' AND c.lastModified < :threshold")
    List<Cart> findAbandonedCarts(@Param("threshold") LocalDateTime threshold);
    @Modifying
    @Query("UPDATE Cart c SET c.status = 'ABANDONED' WHERE c.status = 'ACTIVE' AND c.lastModified < :threshold")
    int markCartsAsAbandoned(@Param("threshold") LocalDateTime threshold);

}
