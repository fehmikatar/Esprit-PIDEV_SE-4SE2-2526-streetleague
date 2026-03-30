package tn.esprit._4se2.pi.restcontrollers;

import io.swagger.v3.oas.annotations.tags.Tag;
import tn.esprit._4se2.pi.entities.Appointment;
import tn.esprit._4se2.pi.dto.AppointmentRequest;
import tn.esprit._4se2.pi.dto.AppointmentResponse;
import tn.esprit._4se2.pi.services.IAppointmentService;
import tn.esprit._4se2.pi.mappers.AppointmentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/appointments")
@Tag(name = "Appointments", description = "📅 Appointment management operations")
@RequiredArgsConstructor
public class AppointmentRestController {

    private final IAppointmentService appointmentService;
    private final AppointmentMapper appointmentMapper;

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> getAllAppointments() {
        List<Appointment> appointments = appointmentService.getAllAppointments();
        List<AppointmentResponse> responses = appointments.stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponse> getAppointmentById(@PathVariable Long id) {
        Appointment appointment = appointmentService.getAppointmentById(id);
        AppointmentResponse response = appointmentMapper.toResponse(appointment);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<AppointmentResponse> createAppointment(@RequestBody AppointmentRequest request) {
        Appointment appointment = appointmentMapper.toEntity(request);
        Appointment created = appointmentService.createAppointment(appointment);
        AppointmentResponse response = appointmentMapper.toResponse(created);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponse> updateAppointment(@PathVariable Long id, @RequestBody AppointmentRequest request) {
        Appointment appointment = appointmentMapper.toEntity(request);
        Appointment updated = appointmentService.updateAppointment(id, appointment);
        AppointmentResponse response = appointmentMapper.toResponse(updated);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByDoctor(@PathVariable Long doctorId) {
        List<Appointment> appointments = appointmentService.getAppointmentsByDoctor(doctorId);
        List<AppointmentResponse> responses = appointments.stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AppointmentResponse>> getAppointmentsByUser(@PathVariable Long userId) {
        List<Appointment> appointments = appointmentService.getAppointmentsByUser(userId);
        List<AppointmentResponse> responses = appointments.stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}