package tn.esprit._4se2.pi.dto;

import lombok.*;
import tn.esprit._4se2.pi.entites.AppointmentStatus;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AppointmentResponse {
    private Long id;
    private Long userId;
    private Long doctorId;
    private LocalDateTime appointmentDate;
    private String reason;
    private AppointmentStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}