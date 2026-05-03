package tn.esprit._4se2.pi.services.MedicalRecord;

import tn.esprit._4se2.pi.dto.MedicalRecord.MedicalRecordRequest;
import tn.esprit._4se2.pi.dto.MedicalRecord.MedicalRecordResponse;
import tn.esprit._4se2.pi.Enum.RecoveryStatus;
import java.util.List;

public interface IMedicalRecordService {
    MedicalRecordResponse createMedicalRecord(MedicalRecordRequest request);
    MedicalRecordResponse getMedicalRecordById(Long id);
    List<MedicalRecordResponse> getAllMedicalRecords();
    List<MedicalRecordResponse> getMedicalRecordsByHealthProfileId(Long healthProfileId);
    List<MedicalRecordResponse> getMedicalRecordsByStatus(RecoveryStatus status);
    MedicalRecordResponse updateMedicalRecord(Long id, MedicalRecordRequest request);
    void deleteMedicalRecord(Long id);
}