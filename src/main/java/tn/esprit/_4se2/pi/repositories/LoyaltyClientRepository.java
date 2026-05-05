// LoyaltyClientRepository.java
package tn.esprit._4se2.pi.repositories;

import tn.esprit._4se2.pi.entities.LoyaltyClient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LoyaltyClientRepository extends JpaRepository<LoyaltyClient, Long> {
    Optional<LoyaltyClient> findByUserId(Long userId);
}