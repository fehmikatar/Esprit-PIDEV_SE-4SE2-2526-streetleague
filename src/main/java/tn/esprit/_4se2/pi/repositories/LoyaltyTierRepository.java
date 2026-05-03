// LoyaltyTierRepository.java
package tn.esprit._4se2.pi.repositories;

import tn.esprit._4se2.pi.entities.LoyaltyTier;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface LoyaltyTierRepository extends JpaRepository<LoyaltyTier, Long> {
    List<LoyaltyTier> findByProgramIdOrderByPointsRequiredAsc(Long programId);
    Optional<LoyaltyTier> findByProgramIdAndPointsRequiredLessThanEqualOrderByPointsRequiredDesc(Long programId, Integer points);
}