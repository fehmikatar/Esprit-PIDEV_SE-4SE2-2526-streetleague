package tn.esprit.se2.laakommanel.pi.entites;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "medical_records")
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "health_profile_id")
    private HealthProfile healthProfile;

    private String diagnosis;
    private LocalDate injuryDate;
    private LocalDate expectedRecoveryDate;
    private LocalDate actualRecoveryDate;
    private String doctorNotes;

    @Enumerated(EnumType.STRING)
    private InjuryType injuryType;

    @Enumerated(EnumType.STRING)
    private RecoveryStatus recoveryStatus;

    private String medicalCertificateUrl;
    private String treatment;
    private String medication;
    private Boolean requiresFollowUp;

    @ManyToOne
    @JoinColumn(name = "treated_by_doctor_id")
    private Doctor treatedBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public HealthProfile getHealthProfile() { return healthProfile; }
    public void setHealthProfile(HealthProfile healthProfile) { this.healthProfile = healthProfile; }

    public String getDiagnosis() { return diagnosis; }
    public void setDiagnosis(String diagnosis) { this.diagnosis = diagnosis; }

    public LocalDate getInjuryDate() { return injuryDate; }
    public void setInjuryDate(LocalDate injuryDate) { this.injuryDate = injuryDate; }

    public LocalDate getExpectedRecoveryDate() { return expectedRecoveryDate; }
    public void setExpectedRecoveryDate(LocalDate expectedRecoveryDate) { this.expectedRecoveryDate = expectedRecoveryDate; }

    public LocalDate getActualRecoveryDate() { return actualRecoveryDate; }
    public void setActualRecoveryDate(LocalDate actualRecoveryDate) { this.actualRecoveryDate = actualRecoveryDate; }

    public String getDoctorNotes() { return doctorNotes; }
    public void setDoctorNotes(String doctorNotes) { this.doctorNotes = doctorNotes; }

    public InjuryType getInjuryType() { return injuryType; }
    public void setInjuryType(InjuryType injuryType) { this.injuryType = injuryType; }

    public RecoveryStatus getRecoveryStatus() { return recoveryStatus; }
    public void setRecoveryStatus(RecoveryStatus recoveryStatus) { this.recoveryStatus = recoveryStatus; }

    public String getMedicalCertificateUrl() { return medicalCertificateUrl; }
    public void setMedicalCertificateUrl(String medicalCertificateUrl) { this.medicalCertificateUrl = medicalCertificateUrl; }

    public String getTreatment() { return treatment; }
    public void setTreatment(String treatment) { this.treatment = treatment; }

    public String getMedication() { return medication; }
    public void setMedication(String medication) { this.medication = medication; }

    public Boolean getRequiresFollowUp() { return requiresFollowUp; }
    public void setRequiresFollowUp(Boolean requiresFollowUp) { this.requiresFollowUp = requiresFollowUp; }

    public Doctor getTreatedBy() { return treatedBy; }
    public void setTreatedBy(Doctor treatedBy) { this.treatedBy = treatedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}