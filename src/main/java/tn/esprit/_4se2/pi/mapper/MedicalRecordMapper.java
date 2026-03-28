package tn.esprit._4se2.pi.mapper;

import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.dto.MedicalRecordRequest;
import tn.esprit._4se2.pi.dto.MedicalRecordResponse;
import tn.esprit._4se2.pi.entites.MedicalRecord;
import tn.esprit._4se2.pi.entites.HealthProfile;
import tn.esprit._4se2.pi.entites.User;
import tn.esprit._4se2.pi.services.IHealthProfileService;
import tn.esprit._4se2.pi.services.IUserService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MedicalRecordMapper {

    private final IHealthProfileService healthProfileService;
    private final IUserService userService;

    public MedicalRecord toEntity(MedicalRecordRequest request) {
        if (request == null) return null;

        MedicalRecord record = new MedicalRecord();
        record.setDiagnosis(request.getDiagnosis());
        record.setInjuryDate(request.getInjuryDate());
        record.setExpectedRecoveryDate(request.getExpectedRecoveryDate());
        record.setDoctorNotes(request.getDoctorNotes());
        record.setInjuryType(request.getInjuryType());
        record.setRecoveryStatus(request.getRecoveryStatus());
        record.setMedicalCertificateUrl(request.getMedicalCertificateUrl());
        record.setTreatment(request.getTreatment());
        record.setMedication(request.getMedication());
        record.setRequiresFollowUp(request.getRequiresFollowUp());

        if (request.getHealthProfileId() != null) {
            HealthProfile profile = new HealthProfile();
            profile.setId(request.getHealthProfileId());
            record.setHealthProfile(profile);
        }

        if (request.getTreatedByDoctorId() != null) {
            User doctor = userService.getUserById(request.getTreatedByDoctorId());
            record.setTreatedBy(doctor);
        }

        return record;
    }

    public MedicalRecordResponse toResponse(MedicalRecord record) {
        if (record == null) return null;

        return MedicalRecordResponse.builder()
                .id(record.getId())
                .healthProfileId(record.getHealthProfile() != null ? record.getHealthProfile().getId() : null)
                .treatedByDoctorId(record.getTreatedBy() != null ? record.getTreatedBy().getId() : null)
                .diagnosis(record.getDiagnosis())
                .injuryDate(record.getInjuryDate())
                .expectedRecoveryDate(record.getExpectedRecoveryDate())
                .actualRecoveryDate(record.getActualRecoveryDate())
                .doctorNotes(record.getDoctorNotes())
                .injuryType(record.getInjuryType())
                .recoveryStatus(record.getRecoveryStatus())
                .medicalCertificateUrl(record.getMedicalCertificateUrl())
                .treatment(record.getTreatment())
                .medication(record.getMedication())
                .requiresFollowUp(record.getRequiresFollowUp())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }
}