package tn.esprit._4se2.pi.services;


import tn.esprit._4se2.pi.entites.Appointment;
import tn.esprit._4se2.pi.entites.AppointmentStatus;
import tn.esprit._4se2.pi.repositories.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements IAppointmentService {
     private final AppointmentRepository appointmentRepository;

        // =============== CRUD de base ===============

        @Override
        public List<Appointment> getAllAppointments() {
            return appointmentRepository.findAll();
        }

        @Override
        public Appointment getAppointmentById(Long id) {
            return appointmentRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));
        }

        @Override
        public Appointment createAppointment(Appointment appointment) {
            // Validation de base
            if (appointment.getAppointmentDate() == null) {
                throw new RuntimeException("Appointment date is required");
            }
            if (appointment.getUser() == null) {
                throw new RuntimeException("User is required");
            }
            if (appointment.getDoctor() == null) {
                throw new RuntimeException("Doctor is required");
            }

            // Définir le statut par défaut si non spécifié
            if (appointment.getStatus() == null) {
                appointment.setStatus(AppointmentStatus.SCHEDULED);
            }

            // Les dates de création/mise à jour sont gérées par @PrePersist dans l'entité
            return appointmentRepository.save(appointment);
        }

        @Override
        public Appointment updateAppointment(Long id, Appointment appointmentDetails) {
            Appointment existingAppointment = getAppointmentById(id);

            // Mise à jour des champs (seulement s'ils sont fournis)
            if (appointmentDetails.getUser() != null) {
                existingAppointment.setUser(appointmentDetails.getUser());
            }
            if (appointmentDetails.getDoctor() != null) {
                existingAppointment.setDoctor(appointmentDetails.getDoctor());
            }
            if (appointmentDetails.getAppointmentDate() != null) {
                existingAppointment.setAppointmentDate(appointmentDetails.getAppointmentDate());
            }
            if (appointmentDetails.getReason() != null) {
                existingAppointment.setReason(appointmentDetails.getReason());
            }
            if (appointmentDetails.getStatus() != null) {
                existingAppointment.setStatus(appointmentDetails.getStatus());
            }
            if (appointmentDetails.getNotes() != null) {
                existingAppointment.setNotes(appointmentDetails.getNotes());
            }

            // updatedAt sera automatiquement mis à jour par @PreUpdate

            return appointmentRepository.save(existingAppointment);
        }

        @Override
        public void deleteAppointment(Long id) {
            Appointment appointment = getAppointmentById(id);
            appointmentRepository.delete(appointment);
        }

        // =============== Recherches spécifiques ===============

        @Override
        public List<Appointment> getAppointmentsByUser(Long userId) {
            if (userId == null) {
                throw new RuntimeException("User ID cannot be null");
            }
            return appointmentRepository.findByUserId(userId);
        }

        @Override
        public List<Appointment> getAppointmentsByDoctor(Long doctorId) {
            if (doctorId == null) {
                throw new RuntimeException("Doctor ID cannot be null");
            }
            return appointmentRepository.findByDoctorId(doctorId);
        }

        @Override
        public List<Appointment> getAppointmentsByStatus(AppointmentStatus status) {
            if (status == null) {
                throw new RuntimeException("Status cannot be null");
            }
            return appointmentRepository.findByStatus(status);
        }

        @Override
        public List<Appointment> getAppointmentsByDateRange(LocalDateTime start, LocalDateTime end) {
            if (start == null || end == null) {
                throw new RuntimeException("Start and end dates cannot be null");
            }
            if (start.isAfter(end)) {
                throw new RuntimeException("Start date must be before end date");
            }
            return appointmentRepository.findByAppointmentDateBetween(start, end);
        }

        @Override
        public List<Appointment> getAppointmentsByDoctorAndDateRange(Long doctorId, LocalDateTime start, LocalDateTime end) {
            if (doctorId == null) {
                throw new RuntimeException("Doctor ID cannot be null");
            }
            if (start == null || end == null) {
                throw new RuntimeException("Start and end dates cannot be null");
            }
            if (start.isAfter(end)) {
                throw new RuntimeException("Start date must be before end date");
            }
            return appointmentRepository.findByDoctorIdAndAppointmentDateBetween(doctorId, start, end);
        }

        @Override
        public List<Appointment> getUserAppointmentsByStatus(Long userId, AppointmentStatus status) {
            if (userId == null) {
                throw new RuntimeException("User ID cannot be null");
            }
            if (status == null) {
                throw new RuntimeException("Status cannot be null");
            }
            return appointmentRepository.findByUserIdAndStatus(userId, status);
        }

        // =============== Statistiques ===============

        @Override
        public Long countAppointmentsByDoctor(Long doctorId) {
            if (doctorId == null) {
                throw new RuntimeException("Doctor ID cannot be null");
            }
            return appointmentRepository.countByDoctorId(doctorId);
        }

        @Override
        public List<Appointment> getUpcomingAppointmentsByUser(Long userId) {
            if (userId == null) {
                throw new RuntimeException("User ID cannot be null");
            }
            return appointmentRepository.findUpcomingAppointmentsByUser(userId);
        }

        @Override
        public List<Appointment> getTodayAppointmentsByDoctor(Long doctorId) {
            if (doctorId == null) {
                throw new RuntimeException("Doctor ID cannot be null");
            }
            return appointmentRepository.findTodayAppointmentsByDoctor(doctorId);
        }

        // =============== Vérifications ===============

        @Override
        public boolean existsById(Long id) {
            return appointmentRepository.existsById(id);
        }

        // =============== Méthodes utilitaires supplémentaires ===============

        /**
         * Annuler un rendez-vous
         */
        public Appointment cancelAppointment(Long id) {
            Appointment appointment = getAppointmentById(id);
            if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
                throw new RuntimeException("Appointment is already cancelled");
            }
            if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
                throw new RuntimeException("Cannot cancel a completed appointment");
            }
            appointment.setStatus(AppointmentStatus.CANCELLED);
            return appointmentRepository.save(appointment);
        }

        /**
         * Confirmer un rendez-vous
         */
        public Appointment confirmAppointment(Long id) {
            Appointment appointment = getAppointmentById(id);
            if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
                throw new RuntimeException("Only scheduled appointments can be confirmed");
            }
            appointment.setStatus(AppointmentStatus.CONFIRMED);
            return appointmentRepository.save(appointment);
        }

        /**
         * Marquer un rendez-vous comme complété
         */
        public Appointment completeAppointment(Long id) {
            Appointment appointment = getAppointmentById(id);
            if (appointment.getStatus() != AppointmentStatus.CONFIRMED) {
                throw new RuntimeException("Only confirmed appointments can be completed");
            }
            appointment.setStatus(AppointmentStatus.COMPLETED);
            return appointmentRepository.save(appointment);
        }

        /**
         * Vérifier la disponibilité d'un docteur à une date donnée
         */
        public boolean isDoctorAvailable(Long doctorId, LocalDateTime dateTime) {
            List<Appointment> appointments = appointmentRepository.findByDoctorIdAndAppointmentDateBetween(
                    doctorId,
                    dateTime.minusMinutes(30), // Vérifier 30min avant
                    dateTime.plusMinutes(30)    // et 30min après
            );
            return appointments.isEmpty();
        }

        /**
         * Obtenir les rendez-vous d'un utilisateur pour une période
         */
        public List<Appointment> getUserAppointmentsInDateRange(Long userId, LocalDateTime start, LocalDateTime end) {
            List<Appointment> allUserAppointments = appointmentRepository.findByUserId(userId);
            return allUserAppointments.stream()
                    .filter(a -> !a.getAppointmentDate().isBefore(start) && !a.getAppointmentDate().isAfter(end))
                    .toList();
        }
    }