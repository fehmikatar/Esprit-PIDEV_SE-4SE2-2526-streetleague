package tn.esprit._4se2.pi.services.Appointment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.dto.Appointment.AppointmentRequest;
import tn.esprit._4se2.pi.dto.Appointment.AppointmentResponse;
import tn.esprit._4se2.pi.entities.Appointment;
import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.entities.Doctor;
import tn.esprit._4se2.pi.Enum.AppointmentStatus;
import tn.esprit._4se2.pi.mappers.AppointmentMapper;
import tn.esprit._4se2.pi.repositories.AppointmentRepository;
import tn.esprit._4se2.pi.repositories.UserRepository;
import tn.esprit._4se2.pi.repositories.DoctorRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AppointmentService implements IAppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final AppointmentMapper appointmentMapper;

    @Override
    public AppointmentResponse createAppointment(AppointmentRequest request) {
        log.info("Creating appointment for user {} with doctor {}", request.getUserId(), request.getDoctorId());

        // Fetch user and doctor
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));
        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + request.getDoctorId()));

        // Map request to entity
        Appointment appointment = appointmentMapper.toEntity(request);
        appointment.setUser(user);
        appointment.setDoctor(doctor);

        // Default status if not set
        if (appointment.getStatus() == null) {
            appointment.setStatus(AppointmentStatus.SCHEDULED);
        }

        Appointment saved = appointmentRepository.save(appointment);
        log.info("Appointment created with id: {}", saved.getId());

        return appointmentMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public AppointmentResponse getAppointmentById(Long id) {
        log.info("Fetching appointment with id: {}", id);
        return appointmentRepository.findById(id)
                .map(appointmentMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAllAppointments() {
        log.info("Fetching all appointments");
        return appointmentRepository.findAll()
                .stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByUserId(Long userId) {
        log.info("Fetching appointments for user id: {}", userId);
        return appointmentRepository.findByUserId(userId)
                .stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByDoctorId(Long doctorId) {
        log.info("Fetching appointments for doctor id: {}", doctorId);
        return appointmentRepository.findByDoctorId(doctorId)
                .stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByStatus(AppointmentStatus status) {
        log.info("Fetching appointments with status: {}", status);
        return appointmentRepository.findByStatus(status)
                .stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AppointmentResponse updateAppointment(Long id, AppointmentRequest request) {
        log.info("Updating appointment with id: {}", id);

        Appointment existing = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));

        // Check if user or doctor changed
        if (!existing.getUser().getId().equals(request.getUserId())) {
            User newUser = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));
            existing.setUser(newUser);
        }
        if (!existing.getDoctor().getId().equals(request.getDoctorId())) {
            Doctor newDoctor = doctorRepository.findById(request.getDoctorId())
                    .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + request.getDoctorId()));
            existing.setDoctor(newDoctor);
        }

        // Update other fields via mapper
        appointmentMapper.updateEntity(request, existing);
        Appointment updated = appointmentRepository.save(existing);
        log.info("Appointment updated with id: {}", id);

        return appointmentMapper.toResponse(updated);
    }

    @Override
    public void deleteAppointment(Long id) {
        log.info("Deleting appointment with id: {}", id);
        if (!appointmentRepository.existsById(id)) {
            throw new RuntimeException("Appointment not found with id: " + id);
        }
        appointmentRepository.deleteById(id);
        log.info("Appointment deleted with id: {}", id);
    }

    @Override
    public AppointmentResponse updateAppointmentStatus(Long id, AppointmentStatus status) {
        log.info("Updating status of appointment {} to {}", id, status);
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));
        appointment.setStatus(status);
        Appointment updated = appointmentRepository.save(appointment);
        return appointmentMapper.toResponse(updated);
    }
}