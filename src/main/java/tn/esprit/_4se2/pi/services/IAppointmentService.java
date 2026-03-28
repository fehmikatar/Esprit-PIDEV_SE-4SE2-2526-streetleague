package tn.esprit._4se2.pi.services;

import tn.esprit._4se2.pi.entites.Appointment;
import tn.esprit._4se2.pi.entites.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface IAppointmentService {
    // CRUD de base
        List<Appointment> getAllAppointments();

        Appointment getAppointmentById(Long id);

        Appointment createAppointment(Appointment appointment);

        Appointment updateAppointment(Long id, Appointment appointment);

        void deleteAppointment(Long id);

        // Recherches spécifiques
        List<Appointment> getAppointmentsByUser(Long userId);

        List<Appointment> getAppointmentsByDoctor(Long doctorId);

        List<Appointment> getAppointmentsByStatus(AppointmentStatus status);

        List<Appointment> getAppointmentsByDateRange(LocalDateTime start, LocalDateTime end);

        List<Appointment> getAppointmentsByDoctorAndDateRange(Long doctorId, LocalDateTime start, LocalDateTime end);

        List<Appointment> getUserAppointmentsByStatus(Long userId, AppointmentStatus status);

        // Statistiques
        Long countAppointmentsByDoctor(Long doctorId);

        List<Appointment> getUpcomingAppointmentsByUser(Long userId);

        List<Appointment> getTodayAppointmentsByDoctor(Long doctorId);

        // Vérifications
        boolean existsById(Long id);
    }