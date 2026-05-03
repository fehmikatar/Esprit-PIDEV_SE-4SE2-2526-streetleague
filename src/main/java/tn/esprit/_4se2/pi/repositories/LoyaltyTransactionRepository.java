// LoyaltyTransactionRepository.java
package tn.esprit._4se2.pi.repositories;

import tn.esprit._4se2.pi.entities.LoyaltyTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LoyaltyTransactionRepository extends JpaRepository<LoyaltyTransaction, Long> {
    List<LoyaltyTransaction> findByClientIdOrderByTransactionDateDesc(Long clientId);
}