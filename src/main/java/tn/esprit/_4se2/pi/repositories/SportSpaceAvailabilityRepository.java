package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.entities.SportSpaceAvailability;
import java.util.List;

@Repository
public interface SportSpaceAvailabilityRepository extends JpaRepository<SportSpaceAvailability, Long> {
    List<SportSpaceAvailability> findBySportSpaceId(Long sportSpaceId);
    List<SportSpaceAvailability> findByStatus(String status);
}