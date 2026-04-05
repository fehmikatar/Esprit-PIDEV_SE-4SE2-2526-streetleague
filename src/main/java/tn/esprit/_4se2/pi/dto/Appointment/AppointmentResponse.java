package tn.esprit._4se2.pi.dto.Appointment;

import lombok.*;
import lombok.experimental.FieldDefaults;
import tn.esprit._4se2.pi.Enum.AppointmentStatus;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppointmentResponse {
    Long id;
    Long userId;
    Long doctorId;
    LocalDateTime appointmentDate;
    String reason;
    AppointmentStatus status;
    String notes;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}