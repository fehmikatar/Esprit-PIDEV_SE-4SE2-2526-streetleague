package tn.esprit._4se2.pi.dto.Appointment;

import lombok.*;
import lombok.experimental.FieldDefaults;
import tn.esprit._4se2.pi.Enum.AppointmentStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppointmentRequest {

    @NotNull(message = "User ID is required")
    Long userId;

    @NotNull(message = "Doctor ID is required")
    Long doctorId;

    @NotNull(message = "Appointment date is required")
    @Future(message = "Appointment date must be in the future")
    LocalDateTime appointmentDate;

    @NotBlank(message = "Reason is required")
    @Size(min = 5, max = 500)
    String reason;

    AppointmentStatus status; // optional, defaults maybe in service

    @Size(max = 1000)
    String notes;
}