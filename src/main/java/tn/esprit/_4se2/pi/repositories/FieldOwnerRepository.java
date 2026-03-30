package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.entities.FieldOwner;
import java.util.Optional;

@Repository
public interface FieldOwnerRepository extends JpaRepository<FieldOwner, Long> {
    Optional<FieldOwner> findByBusinessLicense(String businessLicense);
    Optional<FieldOwner> findByEmail(String email);
}