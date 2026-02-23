package tn.esprit.se2.laakommanel.pi.repositories;


import tn.esprit.se2.laakommanel.pi.entites.MedicalRecord;
import tn.esprit.se2.laakommanel.pi.entites.InjuryType;
import tn.esprit.se2.laakommanel.pi.entites.RecoveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {

    // Trouver par profil santé
    List<MedicalRecord> findByHealthProfileId(Long healthProfileId);

    // Trouver par type de blessure
    List<MedicalRecord> findByInjuryType(InjuryType injuryType);

    // Trouver par statut de récupération
    List<MedicalRecord> findByRecoveryStatus(RecoveryStatus status);

    // Trouver par médecin traitant
    List<MedicalRecord> findByTreatedById(Long doctorId);

    // Blessures actives (non terminées)
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.recoveryStatus != 'COMPLETED'")
    List<MedicalRecord> findActiveInjuries();

    // Compter les blessures actives
    @Query("SELECT COUNT(mr) FROM MedicalRecord mr WHERE mr.recoveryStatus != 'COMPLETED'")
    Long countActiveInjuries();

    // Blessures par période
    List<MedicalRecord> findByInjuryDateBetween(LocalDate startDate, LocalDate endDate);

    // Récupérations prévues dans la semaine
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.expectedRecoveryDate BETWEEN :start AND :end")
    List<MedicalRecord> findRecoveriesBetween(
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    // Statistiques par type de blessure
    @Query("SELECT mr.injuryType, COUNT(mr) FROM MedicalRecord mr GROUP BY mr.injuryType")
    List<Object[]> getInjuryTypeStatistics();

    // Temps de récupération moyen par type de blessure
    @Query("SELECT mr.injuryType, AVG(DATEDIFF(mr.actualRecoveryDate, mr.injuryDate)) " +
            "FROM MedicalRecord mr WHERE mr.actualRecoveryDate IS NOT NULL GROUP BY mr.injuryType")
    List<Object[]> getAverageRecoveryTimeByInjuryType();

    // Patients d'un médecin
    @Query("SELECT DISTINCT mr.healthProfile FROM MedicalRecord mr WHERE mr.treatedBy.id = :doctorId")
    List<Object[]> findPatientsByDoctor(@Param("doctorId") Long doctorId);

    // Recherche avancée
    @Query("SELECT mr FROM MedicalRecord mr WHERE " +
            "(:healthProfileId IS NULL OR mr.healthProfile.id = :healthProfileId) AND " +
            "(:injuryType IS NULL OR mr.injuryType = :injuryType) AND " +
            "(:status IS NULL OR mr.recoveryStatus = :status)")
    List<MedicalRecord> searchMedicalRecords(
            @Param("healthProfileId") Long healthProfileId,
            @Param("injuryType") InjuryType injuryType,
            @Param("status") RecoveryStatus status);
}