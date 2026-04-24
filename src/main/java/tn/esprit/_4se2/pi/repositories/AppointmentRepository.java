package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.dto.AppointmentDetails.AppointmentDetailsDTO;
import tn.esprit._4se2.pi.entities.Appointment;
import tn.esprit._4se2.pi.Enum.AppointmentStatus;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query("SELECT new tn.esprit._4se2.pi.dto.AppointmentDetails.AppointmentDetailsDTO(" +
            "a.id, " +
            "CONCAT(u.firstName, ' ', u.lastName), " +
            "CONCAT(d.firstName, ' ', d.lastName), " +
            "a.reason, a.appointmentDate) " +
            "FROM Appointment a " +
            "JOIN a.user u " +
            "JOIN a.doctor d")
    List<AppointmentDetailsDTO> getAppointmentDetails();

    @Query("SELECT a FROM Appointment a " +
            "JOIN a.user u " +
            "JOIN a.doctor d " +
            "WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(d.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(d.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(a.reason) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Appointment> searchAppointmentsByKeyword(@Param("keyword") String keyword);


    List<Appointment> findByUserId(Long userId);
    List<Appointment> findByDoctorId(Long doctorId);
    List<Appointment> findByStatus(AppointmentStatus status);
    List<Appointment> findByAppointmentDateBetween(LocalDateTime start, LocalDateTime end);
    List<Appointment> findByUserIdAndStatus(Long userId, AppointmentStatus status);
    List<Appointment> findByDoctorIdAndStatus(Long doctorId, AppointmentStatus status);
    List<Appointment> findByAppointmentDateBeforeAndStatusNotIn(LocalDateTime date, List<AppointmentStatus> statuses);
}