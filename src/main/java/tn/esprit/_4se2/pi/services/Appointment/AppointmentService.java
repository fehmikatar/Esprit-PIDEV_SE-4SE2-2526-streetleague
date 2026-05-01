package tn.esprit._4se2.pi.services.Appointment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.dto.Appointment.AppointmentRequest;
import tn.esprit._4se2.pi.dto.Appointment.AppointmentResponse;
import tn.esprit._4se2.pi.dto.AppointmentDetails.AppointmentDetailsDTO;
import tn.esprit._4se2.pi.dto.Notification.NotificationRequest;
import tn.esprit._4se2.pi.entities.Appointment;
import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.entities.Doctor;
import tn.esprit._4se2.pi.Enum.AppointmentStatus;
import tn.esprit._4se2.pi.Enum.Role;
import tn.esprit._4se2.pi.mappers.AppointmentMapper;
import tn.esprit._4se2.pi.repositories.AppointmentRepository;
import tn.esprit._4se2.pi.repositories.UserRepository;
import tn.esprit._4se2.pi.repositories.DoctorRepository;
import tn.esprit._4se2.pi.services.Notification.INotificationService;

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
    private final INotificationService notificationService;

    @Override
    public AppointmentResponse createAppointment(AppointmentRequest request) {
        log.info("Creating appointment for user {} with doctor {}", request.getUserId(), request.getDoctorId());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getUserId()));

        Doctor doctor = doctorRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + request.getDoctorId()));

        if (!doctor.isAvailable()) {
            throw new RuntimeException("Doctor is currently not available for appointments.");
        }

        if (doctor.getWorkingHoursStart() != null && doctor.getWorkingHoursEnd() != null) {
            java.time.LocalTime appointmentTime = request.getAppointmentDate().toLocalTime();
            java.time.LocalTime start = java.time.LocalTime.parse(doctor.getWorkingHoursStart());
            java.time.LocalTime end = java.time.LocalTime.parse(doctor.getWorkingHoursEnd());

            if (appointmentTime.isBefore(start) || appointmentTime.isAfter(end)) {
                throw new RuntimeException("Doctor only works between " + doctor.getWorkingHoursStart() + " and " + doctor.getWorkingHoursEnd());
            }
        }

        List<Appointment> duplicates = appointmentRepository.findByAppointmentDateBetween(
                request.getAppointmentDate().minusMinutes(1),
                request.getAppointmentDate().plusMinutes(1)
        );
        boolean exists = duplicates.stream().anyMatch(a ->
                a.getUser().getId().equals(request.getUserId()) &&
                        a.getDoctor().getId().equals(request.getDoctorId())
        );
        if (exists) {
            throw new RuntimeException("Un rendez-vous identique existe déjà pour cette heure.");
        }

        Appointment appointment = appointmentMapper.toEntity(request);
        appointment.setUser(user);
        appointment.setDoctor(doctor);

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
        return appointmentRepository.findById(id)
                .map(appointmentMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByUserId(Long userId) {
        return appointmentRepository.findByUserId(userId)
                .stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByDoctorId(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId)
                .stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentResponse> getAppointmentsByStatus(AppointmentStatus status) {
        return appointmentRepository.findByStatus(status)
                .stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public AppointmentResponse updateAppointment(Long id, AppointmentRequest request) {
        Appointment existing = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));

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

        appointmentMapper.updateEntity(request, existing);
        return appointmentMapper.toResponse(appointmentRepository.save(existing));
    }

    @Override
    public void deleteAppointment(Long id) {
        if (!appointmentRepository.existsById(id)) {
            throw new RuntimeException("Appointment not found with id: " + id);
        }
        appointmentRepository.deleteById(id);
    }

    @Override
    public AppointmentResponse updateAppointmentStatus(Long id, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));
        appointment.setStatus(status);
        return appointmentMapper.toResponse(appointmentRepository.save(appointment));
    }

    @Override
    public List<AppointmentDetailsDTO> getAppointmentDetails() {
        return appointmentRepository.getAppointmentDetails();
    }

    @Override
    public List<AppointmentResponse> searchAppointments(String keyword) {
        return appointmentRepository.searchAppointmentsByKeyword(keyword).stream()
                .map(appointmentMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void sendFeedback(Long appointmentId, String message) {
        Appointment apt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        String patientName = apt.getUser().getFirstName() + " " + apt.getUser().getLastName();
        String doctorName = apt.getDoctor().getFirstName() + " " + apt.getDoctor().getLastName();
        String notifMessage = "Avis patient sur le RDV #" + appointmentId + " (Dr. " + doctorName + ") par " + patientName + " : " + message;

        // Enregistrer l'avis dans les notes du rendez-vous
        String currentNotes = apt.getNotes();
        String newNotes = (currentNotes != null ? currentNotes + "\n" : "") + "Avis patient: " + message;
        apt.setNotes(newNotes);
        appointmentRepository.save(apt);

        // Trouver les destinataires (admins / field owners)
        List<User> recipients = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.ROLE_FIELD_OWNER || u.getRole() == Role.ROLE_ADMIN)
                .collect(Collectors.toList());

        if (recipients.isEmpty()) {
            recipients.add(apt.getUser());
        }

        for (User recipient : recipients) {
            notificationService.createNotification(NotificationRequest.builder()
                    .userId(recipient.getId())
                    .title("Avis sur rendez-vous")
                    .message(notifMessage)
                    .type("HEALTH_ALERT")
                    .build());
        }
    }

    @Override
    public void updateFeedback(Long appointmentId, String message) {
        Appointment apt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        // Remplacer l'avis existant (supposons que le dernier avis commence par "Avis patient:")
        String notes = apt.getNotes();
        if (notes != null && notes.contains("Avis patient:")) {
            // Supprimer la dernière ligne d'avis (ou remplacer)
            String[] lines = notes.split("\n");
            StringBuilder newNotes = new StringBuilder();
            for (String line : lines) {
                if (!line.trim().startsWith("Avis patient:")) {
                    newNotes.append(line).append("\n");
                }
            }
            newNotes.append("Avis patient (mis à jour): ").append(message);
            apt.setNotes(newNotes.toString().trim());
        } else {
            apt.setNotes((notes != null ? notes + "\n" : "") + "Avis patient: " + message);
        }
        appointmentRepository.save(apt);
        log.info("Updated feedback for appointment {}", appointmentId);
    }

    @Override
    public void deleteFeedback(Long appointmentId) {
        Appointment apt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        String notes = apt.getNotes();
        if (notes != null && notes.contains("Avis patient:")) {
            String[] lines = notes.split("\n");
            StringBuilder newNotes = new StringBuilder();
            for (String line : lines) {
                if (!line.trim().startsWith("Avis patient:")) {
                    newNotes.append(line).append("\n");
                }
            }
            apt.setNotes(newNotes.toString().trim());
            appointmentRepository.save(apt);
            log.info("Deleted feedback for appointment {}", appointmentId);
        }
    }

    @Override
    public void sendGeneralFeedback(Long userId, String message) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String patientName = user.getFirstName() + " " + user.getLastName();
        String notifMessage = "Message général de " + patientName + " : " + message;

        // Trouver les destinataires (admins / field owners)
        List<User> recipients = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.ROLE_FIELD_OWNER || u.getRole() == Role.ROLE_ADMIN)
                .collect(Collectors.toList());

        if (recipients.isEmpty()) {
            recipients.add(user);
        }

        for (User recipient : recipients) {
            notificationService.createNotification(NotificationRequest.builder()
                    .userId(recipient.getId())
                    .title("Nouveau message patient")
                    .message(notifMessage)
                    .type("HEALTH_ALERT")
                    .build());
        }
    }
}