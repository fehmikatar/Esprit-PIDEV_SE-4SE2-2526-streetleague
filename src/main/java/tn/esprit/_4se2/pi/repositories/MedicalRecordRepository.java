package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.entities.MedicalRecord;
import tn.esprit._4se2.pi.Enum.RecoveryStatus;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findByHealthProfileId(Long healthProfileId);
    List<MedicalRecord> findByRecoveryStatus(RecoveryStatus status);
    List<MedicalRecord> findByInjuryDateBetween(LocalDate start, LocalDate end);
}