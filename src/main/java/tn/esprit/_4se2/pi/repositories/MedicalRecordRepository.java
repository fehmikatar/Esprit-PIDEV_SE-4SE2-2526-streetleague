package tn.esprit._4se2.pi.repositories;

import tn.esprit._4se2.pi.entities.MedicalRecord;
import tn.esprit._4se2.pi.entities.InjuryType;
import tn.esprit._4se2.pi.entities.RecoveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    // ✅ CORRIGÉ: Find by health profile
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.healthProfile.id = :healthProfileId")
    MedicalRecord findByHealthProfileId(@Param("healthProfileId") Long healthProfileId);

    // ✅ OK: injuryType est une propriété directe
    List<MedicalRecord> findByInjuryType(InjuryType injuryType);

    // ✅ OK: recoveryStatus est une propriété directe
    List<MedicalRecord> findByRecoveryStatus(RecoveryStatus status);

    // ✅ CORRIGÉ: Find by injury date between
    List<MedicalRecord> findByInjuryDateBetween(LocalDate startDate, LocalDate endDate);

    // ✅ CORRIGÉ: Find by health profile and injury type
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.healthProfile.id = :healthProfileId AND mr.injuryType = :injuryType")
    MedicalRecord findByHealthProfileIdAndInjuryType(@Param("healthProfileId") Long healthProfileId,
                                                     @Param("injuryType") InjuryType injuryType);

    // ✅ OK: search by diagnosis
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.diagnosis LIKE %:keyword%")
    List<MedicalRecord> searchByDiagnosis(@Param("keyword") String keyword);

    // ✅ CORRIGÉ: Find records after a given date
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.healthProfile.id = :healthProfileId AND mr.injuryDate >= :startDate")
    List<MedicalRecord> findHealthProfileRecordsAfterDate(@Param("healthProfileId") Long healthProfileId,
                                                          @Param("startDate") LocalDate startDate);

    // ✅ CORRIGÉ: Find by doctor
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.treatedBy.id = :doctorId")
    List<MedicalRecord> findByDoctorId(@Param("doctorId") Long doctorId);

    // ✅ OK: count by injury type
    Long countByInjuryType(InjuryType injuryType);

    // ✅ CORRIGÉ: Get average recovery time by injury type
    @Query("SELECT AVG(DATEDIFF(mr.actualRecoveryDate, mr.injuryDate)) FROM MedicalRecord mr " +
            "WHERE mr.injuryType = :injuryType AND mr.recoveryStatus = 'RECOVERED' AND mr.actualRecoveryDate IS NOT NULL")
    Double getAverageRecoveryTimeByInjuryType(@Param("injuryType") InjuryType injuryType);

    // ✅ NOUVEAU: Find records requiring follow-up
    List<MedicalRecord> findByRequiresFollowUpTrue();

    // ✅ NOUVEAU: Find records by doctor and date range
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.treatedBy.id = :doctorId AND mr.injuryDate BETWEEN :start AND :end")
    List<MedicalRecord> findByDoctorIdAndDateRange(@Param("doctorId") Long doctorId,
                                                   @Param("start") LocalDate start,
                                                   @Param("end") LocalDate end);

    // ✅ NOUVEAU: Find active records (not yet recovered)
    List<MedicalRecord> findByRecoveryStatusNot(RecoveryStatus status);
}