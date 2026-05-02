package tn.esprit._4se2.pi.dto.MedicalRecord;

import lombok.*;
import lombok.experimental.FieldDefaults;
import tn.esprit._4se2.pi.Enum.InjuryType;
import tn.esprit._4se2.pi.Enum.RecoveryStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MedicalRecordRequest {

    @NotNull(message = "Health profile ID is required")
    Long healthProfileId;

    @NotBlank(message = "Diagnosis is required")
    @Size(min = 3, max = 500)
    String diagnosis;

    @NotNull(message = "Injury date is required")
    @PastOrPresent(message = "Injury date cannot be in the future")
    LocalDate injuryDate;

    LocalDate expectedRecoveryDate;

    LocalDate actualRecoveryDate;

    @Size(max = 1000)
    String doctorNotes;

    InjuryType injuryType;

    RecoveryStatus recoveryStatus;

    @Size(max = 255)
    String medicalCertificateUrl;

    @Size(max = 500)
    String treatment;

    @Size(max = 500)
    String medication;

    Boolean requiresFollowUp;

    Long treatedByDoctorId; // optional
}