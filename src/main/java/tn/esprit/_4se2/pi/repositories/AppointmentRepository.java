package tn.esprit._4se2.pi.repositories;

import tn.esprit._4se2.pi.entities.Appointment;
import tn.esprit._4se2.pi.entities.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    // ✅ Par userId (corrigé)
    @Query("SELECT a FROM Appointment a WHERE a.user.id = :userId")
    List<Appointment> findByUserId(@Param("userId") Long userId);

    // ✅ Par userId et status
    @Query("SELECT a FROM Appointment a WHERE a.user.id = :userId AND a.status = :status")
    List<Appointment> findByUserIdAndStatus(@Param("userId") Long userId,
                                            @Param("status") AppointmentStatus status);

    // ✅ Par doctorId (fonctionne car doctor est une entité)
    List<Appointment> findByDoctorId(Long doctorId);

    // ✅ Par status
    List<Appointment> findByStatus(AppointmentStatus status);

    // ✅ CORRIGÉ: utilisation de appointmentDate au lieu de date
    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate BETWEEN :start AND :end")
    List<Appointment> findByAppointmentDateBetween(@Param("start") LocalDateTime start,
                                                   @Param("end") LocalDateTime end);

    // ✅ Par doctorId et date (corrigé)
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND a.appointmentDate BETWEEN :start AND :end")
    List<Appointment> findByDoctorIdAndAppointmentDateBetween(@Param("doctorId") Long doctorId,
                                                              @Param("start") LocalDateTime start,
                                                              @Param("end") LocalDateTime end);

    // ✅ Count by doctorId
    Long countByDoctorId(Long doctorId);

    // ✅ Rendez-vous à venir pour un utilisateur (corrigé)
    @Query("SELECT a FROM Appointment a WHERE a.user.id = :userId AND a.appointmentDate > CURRENT_TIMESTAMP ORDER BY a.appointmentDate")
    List<Appointment> findUpcomingAppointmentsByUser(@Param("userId") Long userId);

    // ✅ Rendez-vous du jour pour un docteur (corrigé)
    @Query("SELECT a FROM Appointment a WHERE a.doctor.id = :doctorId AND DATE(a.appointmentDate) = CURRENT_DATE")
    List<Appointment> findTodayAppointmentsByDoctor(@Param("doctorId") Long doctorId);
}