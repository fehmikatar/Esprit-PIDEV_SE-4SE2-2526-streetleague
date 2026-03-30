package tn.esprit._4se2.pi.services;

import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.entities.UserRole;
import tn.esprit._4se2.pi.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final UserRepository userRepository;

    // =============== CRUD de base ===============

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    @Override
    public User createUser(User user) {
        if (user.getEmail() != null && userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists: " + user.getEmail());
        }
        user.setCreatedAt(LocalDateTime.now());
        user.setActive(true);
        if (user.getRole() == null) {
            user.setRole(UserRole.PATIENT);
        }
        validateUserByRole(user);
        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, User userDetails) {
        User existingUser = getUserById(id);

        if (userDetails.getFirstName() != null) {
            existingUser.setFirstName(userDetails.getFirstName());
        }
        if (userDetails.getLastName() != null) {
            existingUser.setLastName(userDetails.getLastName());
        }
        if (userDetails.getEmail() != null) {
            if (!userDetails.getEmail().equals(existingUser.getEmail()) &&
                    userRepository.existsByEmail(userDetails.getEmail())) {
                throw new RuntimeException("Email already exists: " + userDetails.getEmail());
            }
            existingUser.setEmail(userDetails.getEmail());
        }
        if (userDetails.getPhone() != null) {
            existingUser.setPhone(userDetails.getPhone());
        }
        if (userDetails.getPasswordHash() != null) {
            existingUser.setPasswordHash(userDetails.getPasswordHash());
        }
        if (userDetails.getSpecialty() != null) {
            existingUser.setSpecialty(userDetails.getSpecialty());
        }
        if (userDetails.getLicenseNumber() != null) {
            existingUser.setLicenseNumber(userDetails.getLicenseNumber());
        }
        if (userDetails.getAddress() != null) {
            existingUser.setAddress(userDetails.getAddress());
        }
        if (userDetails.getRole() != null && userDetails.getRole() != existingUser.getRole()) {
            validateRoleChange(existingUser, userDetails.getRole());
            existingUser.setRole(userDetails.getRole());
        }

        return userRepository.save(existingUser);
    }
    @Override
    public void deleteUser(Long id) {
        User user = getUserById(id);
        userRepository.delete(user);
        userRepository.flush(); // pour forcer l’exécution immédiate
    }
    @Override
    public List<User> searchUsersByName(String name) {
        return userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name);
    }

    @Override
    public List<User> getActiveUsers() {
        return userRepository.findByIsActiveTrue();
    }

    @Override
    public List<User> getInactiveUsers() {
        return userRepository.findByIsActiveFalse();
    }

    @Override
    public List<User> getUsersWithoutHealthProfile() {
        return userRepository.findUsersWithoutHealthProfile();
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public List<User> getUsersByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    @Override
    public List<User> getActiveUsersByRole(UserRole role) {
        return userRepository.findByRoleAndIsActiveTrue(role);
    }

    @Override
    public Long countUsersByRole(UserRole role) {
        return userRepository.countByRole(role);
    }

    // =============== Méthodes spécifiques aux docteurs ===============

    @Override
    public List<User> getAllDoctors() {
        return userRepository.findByRole(UserRole.DOCTOR);
    }

    @Override
    public List<User> getDoctorsBySpecialty(String specialty) {
        return userRepository.findByRoleAndSpecialty(UserRole.DOCTOR, specialty);
    }



    @Override
    public List<Object[]> getDoctorsByAppointmentCount() {
        return userRepository.findDoctorsByAppointmentCount();
    }

    @Override
    public List<User> getAvailableDoctorsToday() {
        return userRepository.findAvailableDoctorsToday();
    }

    @Override
    public List<User> searchDoctors(String keyword) {
        return userRepository.searchDoctors(keyword);
    }

    // =============== Méthodes pour les patients ===============

    @Override
    public List<User> getPatients() {
        return userRepository.findByRole(UserRole.PATIENT);
    }

    @Override
    public List<User> getPatientsWithoutHealthProfile() {
        return userRepository.findPatientsWithoutHealthProfile();
    }

    @Override
    public List<User> getPatientsWithUpcomingAppointments() {
        return userRepository.findPatientsWithUpcomingAppointments();
    }

    @Override
    public List<Object[]> countPatientsByFitnessStatus() {
        return userRepository.countPatientsByFitnessStatus();
    }

    // =============== Gestion du statut actif ===============

    @Override
    public User activateUser(Long id) {
        User user = getUserById(id);
        user.setActive(true);
        return userRepository.save(user);
    }

    @Override
    public User deactivateUser(Long id) {
        User user = getUserById(id);
        user.setActive(false);
        return userRepository.save(user);
    }

    // =============== Vérifications ===============

    @Override
    public boolean isEmailAvailableForRole(String email, UserRole role) {
        return !userRepository.existsByEmailAndRole(email, role);
    }

    @Override
    public boolean existsByLicenseNumber(String licenseNumber) {
        return userRepository.existsByLicenseNumber(licenseNumber);
    }

    // =============== Statistiques avancées ===============

    @Override
    public Long countUsersCreatedBetween(LocalDateTime start, LocalDateTime end) {
        return userRepository.countByCreatedAtBetween(start, end);
    }

    @Override
    public List<Object[]> getUsersStatsByMonth() {
        return userRepository.getUsersStatsByMonth();
    }

    // =============== NOUVELLES MÉTHODES ===============

    @Override
    public List<User> getRecentlyRegistered(LocalDateTime since) {
        if (since == null) {
            since = LocalDateTime.now().minusDays(30);
            log.info("No date provided, using default: last 30 days");
        }
        if (since.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Since date cannot be in the future");
        }
        log.info("Fetching users registered since: {}", since);
        List<User> recentUsers = userRepository.findRecentlyRegistered(since);
        log.info("Found {} users registered since {}", recentUsers.size(), since);
        return recentUsers;
    }

    @Override
    public List<User> advancedSearch(String firstName, String lastName, String email, UserRole role, Boolean active) {
        log.info("Advanced search with criteria - firstName: {}, lastName: {}, email: {}, role: {}, active: {}",
                firstName, lastName, email, role, active);

        if (firstName == null && lastName == null && email == null && role == null && active == null) {
            throw new IllegalArgumentException("At least one search criteria must be provided");
        }

        String trimmedFirstName = firstName != null ? firstName.trim() : null;
        String trimmedLastName = lastName != null ? lastName.trim() : null;
        String trimmedEmail = email != null ? email.trim() : null;

        if (trimmedFirstName != null && trimmedFirstName.isEmpty()) trimmedFirstName = null;
        if (trimmedLastName != null && trimmedLastName.isEmpty()) trimmedLastName = null;
        if (trimmedEmail != null && trimmedEmail.isEmpty()) trimmedEmail = null;

        if (trimmedFirstName == null && trimmedLastName == null && trimmedEmail == null && role == null && active == null) {
            throw new IllegalArgumentException("At least one non-empty search criteria must be provided");
        }

        List<User> results = userRepository.advancedSearch(
                trimmedFirstName, trimmedLastName, trimmedEmail, role, active
        );

        log.info("Advanced search returned {} results", results.size());
        return results;
    }

    // =============== Méthodes utilitaires privées ===============

    private void validateUserByRole(User user) {
        if (user.getRole() == UserRole.DOCTOR) {
            if (user.getSpecialty() == null || user.getSpecialty().trim().isEmpty()) {
                throw new RuntimeException("Specialty is required for doctors");
            }
            if (user.getLicenseNumber() == null || user.getLicenseNumber().trim().isEmpty()) {
                throw new RuntimeException("License number is required for doctors");
            }
        }
    }

    private void validateRoleChange(User user, UserRole newRole) {
        if (user.getRole() == UserRole.DOCTOR && newRole != UserRole.DOCTOR) {
            if (user.getDoctorAppointments() != null && !user.getDoctorAppointments().isEmpty()) {
                throw new RuntimeException("Cannot change role of a doctor with existing appointments");
            }
            if (user.getTreatedRecords() != null && !user.getTreatedRecords().isEmpty()) {
                throw new RuntimeException("Cannot change role of a doctor with existing medical records");
            }
        }
        if (user.getRole() == UserRole.PATIENT && newRole == UserRole.DOCTOR) {
            if (user.getHealthProfile() != null) {
                throw new RuntimeException("Cannot change patient with health profile to doctor");
            }
        }
    }
}