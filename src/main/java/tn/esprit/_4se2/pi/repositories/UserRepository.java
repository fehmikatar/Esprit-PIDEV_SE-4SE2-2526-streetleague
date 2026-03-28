package tn.esprit._4se2.pi.repositories;

import tn.esprit._4se2.pi.entites.User;
import tn.esprit._4se2.pi.entites.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // =============== Recherches de base ===============

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName);
    List<User> findByIsActiveTrue();
    List<User> findByIsActiveFalse();

    // =============== Recherches par rôle ===============

    List<User> findByRole(UserRole role);
    List<User> findByRoleAndIsActiveTrue(UserRole role);
    Long countByRole(UserRole role);
    boolean existsByEmailAndRole(String email, UserRole role);

    // =============== Méthodes spécifiques aux docteurs ===============

    @Query("SELECT u FROM User u WHERE u.role = 'DOCTOR'")
    List<User> findAllDoctors();

    @Query("SELECT u FROM User u WHERE u.role = 'DOCTOR' AND u.specialty = :specialty")
    List<User> findDoctorsBySpecialty(@Param("specialty") String specialty);

    List<User> findByRoleAndSpecialty(UserRole role, String specialty);

    @Query("SELECT u, COUNT(a) as appointmentCount FROM User u " +
            "LEFT JOIN Appointment a ON u.id = a.doctor.id " +
            "WHERE u.role = 'DOCTOR' " +
            "GROUP BY u.id " +
            "ORDER BY appointmentCount DESC")
    List<Object[]> findDoctorsByAppointmentCount();

    @Query("SELECT u FROM User u WHERE u.role = 'DOCTOR' AND u.id NOT IN " +
            "(SELECT a.doctor.id FROM Appointment a WHERE DATE(a.appointmentDate) = CURRENT_DATE)")
    List<User> findAvailableDoctorsToday();

    @Query("SELECT u FROM User u WHERE u.role = 'DOCTOR' AND " +
            "(LOWER(u.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(u.specialty) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<User> searchDoctors(@Param("keyword") String keyword);

    // =============== Méthodes spécifiques aux patients ===============

    @Query("SELECT u FROM User u WHERE u.role = 'PATIENT'")
    List<User> findAllPatients();

    @Query("SELECT u FROM User u WHERE u.role = 'PATIENT' AND u.id NOT IN " +
            "(SELECT hp.user.id FROM HealthProfile hp)")
    List<User> findPatientsWithoutHealthProfile();

    @Query("SELECT DISTINCT u FROM User u JOIN Appointment a ON u.id = a.user.id " +
            "WHERE u.role = 'PATIENT' AND a.appointmentDate > CURRENT_TIMESTAMP")
    List<User> findPatientsWithUpcomingAppointments();

    @Query("SELECT hp.fitnessStatus, COUNT(u) FROM User u " +
            "JOIN HealthProfile hp ON u.id = hp.user.id " +
            "WHERE u.role = 'PATIENT' GROUP BY hp.fitnessStatus")
    List<Object[]> countPatientsByFitnessStatus();

    // =============== Mise à jour du statut ===============

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isActive = :active WHERE u.id = :id")
    void updateActiveStatus(@Param("id") Long id, @Param("active") boolean active);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isActive = true WHERE u.id = :id")
    void activateUser(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isActive = false WHERE u.id = :id")
    void deactivateUser(@Param("id") Long id);

    // =============== Statistiques avancées ===============

    Long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT FUNCTION('MONTH', u.createdAt), FUNCTION('YEAR', u.createdAt), u.role, COUNT(u) " +
            "FROM User u GROUP BY FUNCTION('MONTH', u.createdAt), FUNCTION('YEAR', u.createdAt), u.role " +
            "ORDER BY FUNCTION('YEAR', u.createdAt) DESC, FUNCTION('MONTH', u.createdAt) DESC")
    List<Object[]> getUsersStatsByMonth();

    @Query("SELECT u FROM User u WHERE u.createdAt >= :since ORDER BY u.createdAt DESC")
    List<User> findRecentlyRegistered(@Param("since") LocalDateTime since);

    @Query("SELECT u FROM User u WHERE " +
            "(:firstName IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND " +
            "(:lastName IS NULL OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))) AND " +
            "(:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) AND " +
            "(:role IS NULL OR u.role = :role) AND " +
            "(:active IS NULL OR u.isActive = :active)")
    List<User> advancedSearch(@Param("firstName") String firstName,
                              @Param("lastName") String lastName,
                              @Param("email") String email,
                              @Param("role") UserRole role,
                              @Param("active") Boolean active);

    // =============== Vérifications d'unicité ===============

    boolean existsByLicenseNumber(String licenseNumber);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email AND u.id != :id")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("id") Long id);

    // ✅ CORRIGÉ - Ajout de l'annotation @Query
    @Query("SELECT u FROM User u WHERE u.id NOT IN (SELECT hp.user.id FROM HealthProfile hp)")
    List<User> findUsersWithoutHealthProfile();
}