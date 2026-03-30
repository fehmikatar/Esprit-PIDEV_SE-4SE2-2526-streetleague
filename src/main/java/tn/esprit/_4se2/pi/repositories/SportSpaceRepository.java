package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.entities.SportSpace;
import java.util.List;

@Repository
public interface SportSpaceRepository extends JpaRepository<SportSpace, Long> {
    List<SportSpace> findByFieldOwnerId(Long fieldOwnerId);
    List<SportSpace> findBySportType(String sportType);
    List<SportSpace> findByIsAvailableTrue();
    List<SportSpace> findByLocationContainingIgnoreCase(String location);
}