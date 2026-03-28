package tn.esprit._4se2.pi.services;

import tn.esprit._4se2.pi.entites.MedicalRecord;
import tn.esprit._4se2.pi.entites.InjuryType;
import tn.esprit._4se2.pi.entites.RecoveryStatus;
import tn.esprit._4se2.pi.repositories.MedicalRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class MedicalRecordServiceImpl implements IMedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;

    // =============== CRUD de base ===============

    @Override
    public List<MedicalRecord> getAllMedicalRecords() {
        return medicalRecordRepository.findAll();
    }

    @Override
    public MedicalRecord getMedicalRecordById(Long id) {
        return medicalRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MedicalRecord not found with id: " + id));
    }

    @Override
    public MedicalRecord createMedicalRecord(MedicalRecord medicalRecord) {
        // Validation
        if (medicalRecord.getHealthProfile() == null) {
            throw new RuntimeException("Health profile is required");
        }
        if (medicalRecord.getInjuryDate() == null) {
            medicalRecord.setInjuryDate(LocalDate.now());
        }
        if (medicalRecord.getRecoveryStatus() == null) {
            medicalRecord.setRecoveryStatus(RecoveryStatus.PENDING);
        }

        return medicalRecordRepository.save(medicalRecord);
    }

    @Override
    public MedicalRecord updateMedicalRecord(Long id, MedicalRecord recordDetails) {
        MedicalRecord existingRecord = getMedicalRecordById(id);

        // Mise à jour conditionnelle
        if (recordDetails.getDiagnosis() != null) {
            existingRecord.setDiagnosis(recordDetails.getDiagnosis());
        }
        if (recordDetails.getTreatment() != null) {
            existingRecord.setTreatment(recordDetails.getTreatment());
        }
        if (recordDetails.getMedication() != null) {
            existingRecord.setMedication(recordDetails.getMedication());
        }
        if (recordDetails.getInjuryType() != null) {
            existingRecord.setInjuryType(recordDetails.getInjuryType());
        }
        if (recordDetails.getRecoveryStatus() != null) {
            existingRecord.setRecoveryStatus(recordDetails.getRecoveryStatus());
            // Si le statut devient RECOVERED, mettre à jour actualRecoveryDate
            if (recordDetails.getRecoveryStatus() == RecoveryStatus.REFERRED &&
                    existingRecord.getActualRecoveryDate() == null) {
                existingRecord.setActualRecoveryDate(LocalDate.now());
            }
        }
        if (recordDetails.getExpectedRecoveryDate() != null) {
            existingRecord.setExpectedRecoveryDate(recordDetails.getExpectedRecoveryDate());
        }
        if (recordDetails.getDoctorNotes() != null) {
            existingRecord.setDoctorNotes(recordDetails.getDoctorNotes());
        }
        if (recordDetails.getRequiresFollowUp() != null) {
            existingRecord.setRequiresFollowUp(recordDetails.getRequiresFollowUp());
        }
        if (recordDetails.getTreatedBy() != null) {
            existingRecord.setTreatedBy(recordDetails.getTreatedBy());
        }

        return medicalRecordRepository.save(existingRecord);
    }

    @Override
    public void deleteMedicalRecord(Long id) {
        MedicalRecord record = getMedicalRecordById(id);
        medicalRecordRepository.delete(record);
    }

    // =============== Recherches spécifiques ===============

    @Override
    public MedicalRecord getMedicalRecordByHealthProfile(Long healthProfileId) {
        if (healthProfileId == null) {
            throw new RuntimeException("Health profile ID cannot be null");
        }
        return medicalRecordRepository.findByHealthProfileId(healthProfileId);
    }

    @Override
    public List<MedicalRecord> getMedicalRecordsByInjuryType(InjuryType injuryType) {
        if (injuryType == null) {
            throw new RuntimeException("Injury type cannot be null");
        }
        return medicalRecordRepository.findByInjuryType(injuryType);
    }

    @Override
    public List<MedicalRecord> getMedicalRecordsByRecoveryStatus(RecoveryStatus status) {
        if (status == null) {
            throw new RuntimeException("Recovery status cannot be null");
        }
        return medicalRecordRepository.findByRecoveryStatus(status);
    }

    @Override
    public List<MedicalRecord> getMedicalRecordsByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new RuntimeException("Start and end dates cannot be null");
        }
        if (startDate.isAfter(endDate)) {
            throw new RuntimeException("Start date must be before end date");
        }
        return medicalRecordRepository.findByInjuryDateBetween(startDate, endDate);
    }

    @Override
    public MedicalRecord getMedicalRecordByHealthProfileAndInjuryType(Long healthProfileId, InjuryType injuryType) {
        if (healthProfileId == null) {
            throw new RuntimeException("Health profile ID cannot be null");
        }
        if (injuryType == null) {
            throw new RuntimeException("Injury type cannot be null");
        }
        return medicalRecordRepository.findByHealthProfileIdAndInjuryType(healthProfileId, injuryType);
    }

    @Override
    public List<MedicalRecord> searchMedicalRecordsByDiagnosis(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new RuntimeException("Search keyword cannot be empty");
        }
        return medicalRecordRepository.searchByDiagnosis(keyword);
    }

    @Override
    public List<MedicalRecord> getMedicalRecordsByDoctor(Long doctorId) {
        if (doctorId == null) {
            throw new RuntimeException("Doctor ID cannot be null");
        }
        return medicalRecordRepository.findByDoctorId(doctorId);
    }

    @Override
    public List<MedicalRecord> getMedicalRecordsRequiringFollowUp() {
        return medicalRecordRepository.findByRequiresFollowUpTrue();
    }

    @Override
    public List<MedicalRecord> getActiveMedicalRecords() {
        return medicalRecordRepository.findByRecoveryStatusNot(RecoveryStatus.REFERRED);
    }

    @Override
    public Double getAverageRecoveryTimeByInjuryType(InjuryType injuryType) {
        if (injuryType == null) {
            throw new RuntimeException("Injury type cannot be null");
        }
        return medicalRecordRepository.getAverageRecoveryTimeByInjuryType(injuryType);
    }
}