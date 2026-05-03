// tn.esprit._4se2.pi.dto.AppointmentDetailsDTO.java
package tn.esprit._4se2.pi.dto.AppointmentDetails;

import java.time.LocalDateTime;

public class AppointmentDetailsDTO {
    private Long appointmentId;
    private String patientName;
    private String doctorName;
    private String reason;
    private LocalDateTime appointmentDate;

    // Constructeur avec tous les champs (obligatoire pour la projection)
    public AppointmentDetailsDTO(Long appointmentId, String patientName, String doctorName, String reason, LocalDateTime appointmentDate) {
        this.appointmentId = appointmentId;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.reason = reason;
        this.appointmentDate = appointmentDate;
    }

    // Getters (nécessaires pour la sérialisation JSON)
    public Long getAppointmentId() { return appointmentId; }
    public String getPatientName() { return patientName; }
    public String getDoctorName() { return doctorName; }
    public String getReason() { return reason; }
    public LocalDateTime getAppointmentDate() { return appointmentDate; }

    // Setters (optionnels, mais recommandés pour la sérialisation)
    public void setAppointmentId(Long appointmentId) { this.appointmentId = appointmentId; }
    public void setPatientName(String patientName) { this.patientName = patientName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    public void setReason(String reason) { this.reason = reason; }
    public void setAppointmentDate(LocalDateTime appointmentDate) { this.appointmentDate = appointmentDate; }
}