// LoyaltyProgramRepository.java
package tn.esprit._4se2.pi.repositories;

import tn.esprit._4se2.pi.entities.LoyaltyProgram;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LoyaltyProgramRepository extends JpaRepository<LoyaltyProgram, Long> {
    Optional<LoyaltyProgram> findFirstByActiveTrue();
}