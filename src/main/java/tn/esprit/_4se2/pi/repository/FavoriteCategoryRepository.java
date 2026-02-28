package tn.esprit._4se2.pi.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit._4se2.pi.entities.User;
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.entities.FavoriteCategory;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteCategoryRepository extends JpaRepository<FavoriteCategory, Long> {

    // Pour getUserCategories
    List<FavoriteCategory> findByUser(User user);

    // Pour addCategory (vérification)
    boolean existsByUserIdAndName(Long userId, String name);

    // Pour find category by id (existe déjà avec findById)
    Optional<FavoriteCategory> findById(Long id);
}
