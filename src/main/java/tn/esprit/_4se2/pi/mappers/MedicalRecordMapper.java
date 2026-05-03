package tn.esprit._4se2.pi.mappers;

import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.dto.MedicalRecord.MedicalRecordRequest;
import tn.esprit._4se2.pi.dto.MedicalRecord.MedicalRecordResponse;
import tn.esprit._4se2.pi.entities.MedicalRecord;

@Component
public class MedicalRecordMapper {

    public MedicalRecord toEntity(MedicalRecordRequest request) {
        if (request == null) return null;

        MedicalRecord record = new MedicalRecord();
        record.setDiagnosis(request.getDiagnosis());
        record.setInjuryDate(request.getInjuryDate());
        record.setExpectedRecoveryDate(request.getExpectedRecoveryDate());
        record.setActualRecoveryDate(request.getActualRecoveryDate());
        record.setDoctorNotes(request.getDoctorNotes());
        record.setInjuryType(request.getInjuryType());
        record.setRecoveryStatus(request.getRecoveryStatus());
        record.setMedicalCertificateUrl(request.getMedicalCertificateUrl());
        record.setTreatment(request.getTreatment());
        record.setMedication(request.getMedication());
        record.setRequiresFollowUp(request.getRequiresFollowUp());
        return record;
    }

    public MedicalRecordResponse toResponse(MedicalRecord entity) {
        if (entity == null) return null;

        return MedicalRecordResponse.builder()
                .id(entity.getId())
                .healthProfileId(entity.getHealthProfile() != null ? entity.getHealthProfile().getId() : null)
                .diagnosis(entity.getDiagnosis())
                .injuryDate(entity.getInjuryDate())
                .expectedRecoveryDate(entity.getExpectedRecoveryDate())
                .actualRecoveryDate(entity.getActualRecoveryDate())
                .doctorNotes(entity.getDoctorNotes())
                .injuryType(entity.getInjuryType())
                .recoveryStatus(entity.getRecoveryStatus())
                .medicalCertificateUrl(entity.getMedicalCertificateUrl())
                .treatment(entity.getTreatment())
                .medication(entity.getMedication())
                .requiresFollowUp(entity.getRequiresFollowUp())
                .treatedByDoctorId(entity.getTreatedBy() != null ? entity.getTreatedBy().getId() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public void updateEntity(MedicalRecordRequest request, MedicalRecord record) {
        if (request == null || record == null) return;

        record.setDiagnosis(request.getDiagnosis());
        record.setInjuryDate(request.getInjuryDate());
        record.setExpectedRecoveryDate(request.getExpectedRecoveryDate());
        record.setActualRecoveryDate(request.getActualRecoveryDate());
        record.setDoctorNotes(request.getDoctorNotes());
        record.setInjuryType(request.getInjuryType());
        record.setRecoveryStatus(request.getRecoveryStatus());
        record.setMedicalCertificateUrl(request.getMedicalCertificateUrl());
        record.setTreatment(request.getTreatment());
        record.setMedication(request.getMedication());
        record.setRequiresFollowUp(request.getRequiresFollowUp());
    }
}