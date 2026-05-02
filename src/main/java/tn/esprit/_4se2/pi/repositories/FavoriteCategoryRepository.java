package tn.esprit._4se2.pi.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit._4se2.pi.entities.User;
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.entities.FavoriteCategory;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteCategoryRepository extends JpaRepository<FavoriteCategory, Long> {

    List<FavoriteCategory> findByUser(User user);

    boolean existsByUserIdAndName(Long userId, String name);

    Optional<FavoriteCategory> findById(Long id);

    List<FavoriteCategory> findByUserIdAndCreatedAtAfter(Long userId, java.time.LocalDateTime after);

    @Query("SELECT fc.name, COUNT(f.id) " +
           "FROM FavoriteCategory fc " +
           "JOIN fc.favorites f " +                
           "WHERE fc.user.id = :userId " +
           "GROUP BY fc.name")
    List<Object[]> getUserStatsByCategory(@Param("userId") Long userId);
}
