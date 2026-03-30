package tn.esprit._4se2.pi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.entities.SavedCart;
import tn.esprit._4se2.pi.entities.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedCartRepository extends JpaRepository<SavedCart, Long> {

    List<SavedCart> findByUser(User user);

    Optional<SavedCart> findByShareToken(String shareToken);

    List<SavedCart> findByUserId(Long userId);
}
