package tn.esprit._4se2.pi.dto.MedicalRecord;

import lombok.*;
import lombok.experimental.FieldDefaults;
import tn.esprit._4se2.pi.Enum.InjuryType;
import tn.esprit._4se2.pi.Enum.RecoveryStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MedicalRecordResponse {
    Long id;
    Long healthProfileId;
    String diagnosis;
    LocalDate injuryDate;
    LocalDate expectedRecoveryDate;
    LocalDate actualRecoveryDate;
    String doctorNotes;
    InjuryType injuryType;
    RecoveryStatus recoveryStatus;
    String medicalCertificateUrl;
    String treatment;
    String medication;
    Boolean requiresFollowUp;
    Long treatedByDoctorId;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}