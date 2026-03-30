package tn.esprit._4se2.pi.dto.MedicalRecord;

import lombok.*;
import tn.esprit._4se2.pi.entities.InjuryType;
import tn.esprit._4se2.pi.entities.RecoveryStatus;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalRecordRequest {
    private Long healthProfileId;
    private Long treatedByDoctorId;
    private String diagnosis;
    private LocalDate injuryDate;
    private LocalDate expectedRecoveryDate;
    private String doctorNotes;
    private InjuryType injuryType;
    private RecoveryStatus recoveryStatus;
    private String medicalCertificateUrl;
    private String treatment;
    private String medication;
    private Boolean requiresFollowUp;
}