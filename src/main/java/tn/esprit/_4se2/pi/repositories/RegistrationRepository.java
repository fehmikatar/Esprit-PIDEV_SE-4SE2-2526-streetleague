package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit._4se2.pi.entities.Registration;
import tn.esprit._4se2.pi.Enum.RegistrationStatus;

import java.util.List;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    List<Registration> findByCompetitionId(Long competitionId);
    List<Registration> findByStatus(RegistrationStatus status);
    List<Registration> findByCompetitionIdAndStatus(Long competitionId, RegistrationStatus status);
}
