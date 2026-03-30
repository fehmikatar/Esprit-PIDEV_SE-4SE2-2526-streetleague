package tn.esprit._4se2.pi.dto.Appointment;

import lombok.*;
import tn.esprit._4se2.pi.entities.AppointmentStatus;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentRequest {
    private Long userId;
    private Long doctorId;
    private LocalDateTime appointmentDate;
    private String reason;
    private AppointmentStatus status;
    private String notes;
}