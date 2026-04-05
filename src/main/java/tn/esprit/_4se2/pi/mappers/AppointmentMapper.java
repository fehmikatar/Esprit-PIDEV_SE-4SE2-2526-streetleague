package tn.esprit._4se2.pi.mappers;

import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.dto.Appointment.AppointmentRequest;
import tn.esprit._4se2.pi.dto.Appointment.AppointmentResponse;
import tn.esprit._4se2.pi.entities.Appointment;

@Component
public class AppointmentMapper {

    public Appointment toEntity(AppointmentRequest request) {
        if (request == null) return null;

        Appointment appointment = new Appointment();
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setReason(request.getReason());
        appointment.setStatus(request.getStatus()); // can be null, service will set default
        appointment.setNotes(request.getNotes());
        // user and doctor will be set by service (since they need to be fetched from DB)
        return appointment;
    }

    public AppointmentResponse toResponse(Appointment entity) {
        if (entity == null) return null;

        return AppointmentResponse.builder()
                .id(entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .doctorId(entity.getDoctor() != null ? entity.getDoctor().getId() : null)
                .appointmentDate(entity.getAppointmentDate())
                .reason(entity.getReason())
                .status(entity.getStatus())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public void updateEntity(AppointmentRequest request, Appointment appointment) {
        if (request == null || appointment == null) return;

        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setReason(request.getReason());
        appointment.setStatus(request.getStatus());
        appointment.setNotes(request.getNotes());
        // Relationships (user, doctor) are not updated via mapper, service will handle if needed
    }
}