package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.entities.SportCommunity;

import java.util.Optional;

@Repository
public interface SportCommunityRepository extends JpaRepository<SportCommunity, Long> {

    Optional<SportCommunity> findBySportCategoryId(Long sportCategoryId);
}
