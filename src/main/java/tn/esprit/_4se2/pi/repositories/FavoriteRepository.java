package tn.esprit._4se2.pi.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.entities.Favorite;
import tn.esprit._4se2.pi.entities.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {


    Page<Favorite> findByUser(User user, Pageable pageable);


    Optional<Favorite> findByUserIdAndProductId(Long userId, Long productId);

    @Query("SELECT f FROM Favorite f WHERE f.user.id = :userId AND f.category.id = :categoryId")
    List<Favorite> findByUserIdAndCategoryId(@Param("userId") Long userId,
                                             @Param("categoryId") Long categoryId);


    boolean existsByUserIdAndProductId(Long userId, Long productId);

    List<Favorite> findByUserIdAndAddedAtAfter(Long userId, java.time.LocalDateTime after);

    List<Favorite> findByUserId(Long userId);


    @Query("SELECT cat.name, COUNT(f.id) " +
           "FROM Favorite f " +
           "JOIN f.category cat " +
           "GROUP BY cat.name")
    List<Object[]> countFavoritesByCategory();

    @Query("SELECT COUNT(f) FROM Favorite f WHERE f.product.id = :productId")
    long countByProductId(@Param("productId") Long productId);

    List<Favorite> findByProductNomContainingIgnoreCaseAndProductCategoryNomContainingIgnoreCase(
            String productName, String categoryName);

    List<Favorite> findByProductId(Long productId);
}
