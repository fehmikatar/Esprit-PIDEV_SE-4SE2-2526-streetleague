package tn.esprit._4se2.pi.repository;

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

    // Pour getUserFavorites
    Page<Favorite> findByUser(User user, Pageable pageable);

    // Pour removeFromFavorites
    Optional<Favorite> findByUserIdAndProductId(Long userId, Long productId);

    // Pour getUserFavoritesByCategory
    @Query("SELECT f FROM Favorite f WHERE f.user.id = :userId AND f.category.id = :categoryId")
    List<Favorite> findByUserIdAndCategoryId(@Param("userId") Long userId,
                                             @Param("categoryId") Long categoryId);

    // Pour addToFavorites (vérification)
    boolean existsByUserIdAndProductId(Long userId, Long productId);

    // Pour getFavoritesCountByProduct
    @Query("SELECT COUNT(f) FROM Favorite f WHERE f.product.id = :productId")
    long countByProductId(@Param("productId") Long productId);
}
