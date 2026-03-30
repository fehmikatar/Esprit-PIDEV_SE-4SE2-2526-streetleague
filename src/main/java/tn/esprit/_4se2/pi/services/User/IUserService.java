package tn.esprit._4se2.pi.services.User;

import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.entities.UserRole;

import java.time.LocalDateTime;
import java.util.List;

public interface IUserService {
    // CRUD de base
    List<User> getAllUsers();
    User getUserById(Long id);
    User getUserByEmail(String email);
    User createUser(User user);
    User updateUser(Long id, User user);
    void deleteUser(Long id);

    // Recherches
    List<User> searchUsersByName(String name);
    List<User> getActiveUsers();
    List<User> getUsersWithoutHealthProfile();
    boolean existsByEmail(String email);

    // ✅ Méthodes spécifiques aux docteurs
    List<User> getAllDoctors();
    List<User> getDoctorsBySpecialty(String specialty);

    List<Object[]> getDoctorsByAppointmentCount();

    // ✅ Méthodes pour les patients
    List<User> getPatients();

    // ✅ Méthodes utilitaires
    User activateUser(Long id);
    User deactivateUser(Long id);
    boolean isEmailAvailableForRole(String email, UserRole role);

    List<User> getRecentlyRegistered(LocalDateTime since);

    List<User> advancedSearch(String firstName, String lastName, String email, UserRole role, Boolean active);

    List<User> getInactiveUsers();

    List<User> getUsersByRole(UserRole role);

    List<User> getActiveUsersByRole(UserRole role);

    Long countUsersByRole(UserRole role);

    List<User> getAvailableDoctorsToday();

    List<User> searchDoctors(String keyword);

    List<User> getPatientsWithoutHealthProfile();

    List<User> getPatientsWithUpcomingAppointments();

    List<Object[]> countPatientsByFitnessStatus();

    boolean existsByLicenseNumber(String licenseNumber);

    Long countUsersCreatedBetween(LocalDateTime start, LocalDateTime end);

    List<Object[]> getUsersStatsByMonth();
}