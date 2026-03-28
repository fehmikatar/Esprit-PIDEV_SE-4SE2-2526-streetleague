package tn.esprit._4se2.pi.mapper;

import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.dto.AppointmentRequest;
import tn.esprit._4se2.pi.dto.AppointmentResponse;
import tn.esprit._4se2.pi.entites.Appointment;
import tn.esprit._4se2.pi.entites.User;
import tn.esprit._4se2.pi.services.IUserService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AppointmentMapper {

    private final IUserService userService;

    public Appointment toEntity(AppointmentRequest request) {
        if (request == null) return null;

        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setReason(request.getReason());
        appointment.setStatus(request.getStatus());
        appointment.setNotes(request.getNotes());

        if (request.getUserId() != null) {
            User user = userService.getUserById(request.getUserId());
            appointment.setUser(user);
        }

        if (request.getDoctorId() != null) {
            User doctor = userService.getUserById(request.getDoctorId());
            appointment.setDoctor(doctor);
        }

        return appointment;
    }

    public AppointmentResponse toResponse(Appointment appointment) {
        if (appointment == null) return null;

        return AppointmentResponse.builder()
                .id(appointment.getId())
                .userId(appointment.getUser() != null ? appointment.getUser().getId() : null)
                .doctorId(appointment.getDoctor() != null ? appointment.getDoctor().getId() : null)
                .appointmentDate(appointment.getAppointmentDate())
                .reason(appointment.getReason())
                .status(appointment.getStatus())
                .notes(appointment.getNotes())
                .createdAt(appointment.getCreatedAt())
                .updatedAt(appointment.getUpdatedAt())
                .build();
    }
}