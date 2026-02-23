package tn.esprit.se2.laakommanel.pi.repositories;

import tn.esprit.se2.laakommanel.pi.entites.Appointment;
import tn.esprit.se2.laakommanel.pi.entites.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // Trouver par utilisateur
    List<Appointment> findByUserId(Long userId);

    // Trouver par docteur
    List<Appointment> findByDoctorId(Long doctorId);

    // Trouver par statut
    List<Appointment> findByStatus(AppointmentStatus status);

    // Trouver par date
    List<Appointment> findByAppointmentDateBetween(LocalDateTime start, LocalDateTime end);

    // Rendez-vous du jour
    @Query("SELECT a FROM Appointment a WHERE DATE(a.appointmentDate) = CURRENT_DATE")
    List<Appointment> findTodayAppointments();

    // Compter les rendez-vous du jour
    @Query("SELECT COUNT(a) FROM Appointment a WHERE DATE(a.appointmentDate) = CURRENT_DATE")
    Long countTodayAppointments();

    // Rendez-vous à venir (futurs)
    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate > :now AND a.status = 'SCHEDULED'")
    List<Appointment> findUpcomingAppointments(@Param("now") LocalDateTime now);

    // Rendez-vous passés
    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate < :now")
    List<Appointment> findPastAppointments(@Param("now") LocalDateTime now);

    // Vérifier disponibilité
    @Query("SELECT COUNT(a) FROM Appointment a WHERE " +
            "a.doctor.id = :doctorId AND " +
            "a.appointmentDate BETWEEN :start AND :end AND " +
            "a.status != 'CANCELLED'")
    Long countAppointmentsByDoctorAndTimeSlot(
            @Param("doctorId") Long doctorId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    // Planning d'un docteur pour une journée
    @Query("SELECT a FROM Appointment a WHERE " +
            "a.doctor.id = :doctorId AND " +
            "DATE(a.appointmentDate) = DATE(:date) " +
            "ORDER BY a.appointmentDate")
    List<Appointment> findDoctorSchedule(
            @Param("doctorId") Long doctorId,
            @Param("date") LocalDateTime date);

    // Rendez-vous par mois
    @Query("SELECT MONTH(a.appointmentDate), COUNT(a) FROM Appointment a " +
            "WHERE YEAR(a.appointmentDate) = :year GROUP BY MONTH(a.appointmentDate)")
    List<Object[]> getMonthlyAppointmentStats(@Param("year") int year);
}