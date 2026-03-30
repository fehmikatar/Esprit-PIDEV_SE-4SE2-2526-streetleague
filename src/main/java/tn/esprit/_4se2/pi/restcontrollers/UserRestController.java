package tn.esprit._4se2.pi.restcontrollers;

import io.swagger.v3.oas.annotations.tags.Tag;
import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.entities.UserRole;
import tn.esprit._4se2.pi.dto.UserRequest;
import tn.esprit._4se2.pi.dto.UserResponse;
import tn.esprit._4se2.pi.services.IUserService;
import tn.esprit._4se2.pi.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User", description = "👤 User management operations")
public class UserRestController {

    private final IUserService userService;
    private final UserMapper userMapper;

    // =============== CRUD de base ===============

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("GET /api/users - Fetching all users");
        List<User> users = userService.getAllUsers();
        List<UserResponse> responses = users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        log.info("GET /api/users/{} - Fetching user by ID", id);
        User user = userService.getUserById(id);
        UserResponse response = userMapper.toResponse(user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        log.info("GET /api/users/email/{} - Fetching user by email", email);
        User user = userService.getUserByEmail(email);
        UserResponse response = userMapper.toResponse(user);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest request) {
        log.info("POST /api/users - Creating new user with email: {}", request.getEmail());
        User user = userMapper.toEntity(request);
        User createdUser = userService.createUser(user);
        UserResponse response = userMapper.toResponse(createdUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UserRequest request) {
        log.info("PUT /api/users/{} - Updating user", id);
        User user = userMapper.toEntity(request);
        User updatedUser = userService.updateUser(id, user);
        UserResponse response = userMapper.toResponse(updatedUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("DELETE /api/users/{} - Deleting (deactivating) user", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // =============== Recherches ===============

    @GetMapping("/search")
    public ResponseEntity<List<UserResponse>> searchUsers(@RequestParam String name) {
        log.info("GET /api/users/search - Searching users with name: {}", name);
        List<User> users = userService.searchUsersByName(name);
        List<UserResponse> responses = users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/active")
    public ResponseEntity<List<UserResponse>> getActiveUsers() {
        log.info("GET /api/users/active - Fetching all active users");
        List<User> users = userService.getActiveUsers();
        List<UserResponse> responses = users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/inactive")
    public ResponseEntity<List<UserResponse>> getInactiveUsers() {
        log.info("GET /api/users/inactive - Fetching all inactive users");
        List<User> users = userService.getInactiveUsers();
        List<UserResponse> responses = users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/without-profile")
    public ResponseEntity<List<UserResponse>> getUsersWithoutHealthProfile() {
        log.info("GET /api/users/without-profile - Fetching users without health profile");
        List<User> users = userService.getUsersWithoutHealthProfile();
        List<UserResponse> responses = users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable UserRole role) {
        log.info("GET /api/users/role/{} - Fetching users by role", role);
        List<User> users = userService.getUsersByRole(role);
        List<UserResponse> responses = users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/role/{role}/active")
    public ResponseEntity<List<UserResponse>> getActiveUsersByRole(@PathVariable UserRole role) {
        log.info("GET /api/users/role/{}/active - Fetching active users by role", role);
        List<User> users = userService.getActiveUsersByRole(role);
        List<UserResponse> responses = users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/role/{role}/count")
    public ResponseEntity<Long> countUsersByRole(@PathVariable UserRole role) {
        log.info("GET /api/users/role/{}/count - Counting users by role", role);
        Long count = userService.countUsersByRole(role);
        return ResponseEntity.ok(count);
    }

    // =============== Méthodes spécifiques aux docteurs ===============

    @GetMapping("/doctors")
    public ResponseEntity<List<UserResponse>> getAllDoctors() {
        log.info("GET /api/users/doctors - Fetching all doctors");
        List<User> doctors = userService.getAllDoctors();
        List<UserResponse> responses = doctors.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/doctors/specialty/{specialty}")
    public ResponseEntity<List<UserResponse>> getDoctorsBySpecialty(@PathVariable String specialty) {
        log.info("GET /api/users/doctors/specialty/{} - Fetching doctors by specialty", specialty);
        List<User> doctors = userService.getDoctorsBySpecialty(specialty);
        List<UserResponse> responses = doctors.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/doctors/available-today")
    public ResponseEntity<List<UserResponse>> getAvailableDoctorsToday() {
        log.info("GET /api/users/doctors/available-today - Fetching available doctors today");
        List<User> doctors = userService.getAvailableDoctorsToday();
        List<UserResponse> responses = doctors.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/doctors/search")
    public ResponseEntity<List<UserResponse>> searchDoctors(@RequestParam String keyword) {
        log.info("GET /api/users/doctors/search - Searching doctors with keyword: {}", keyword);
        List<User> doctors = userService.searchDoctors(keyword);
        List<UserResponse> responses = doctors.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // =============== Méthodes pour les patients ===============

    @GetMapping("/patients")
    public ResponseEntity<List<UserResponse>> getPatients() {
        log.info("GET /api/users/patients - Fetching all patients");
        List<User> patients = userService.getPatients();
        List<UserResponse> responses = patients.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/patients/without-profile")
    public ResponseEntity<List<UserResponse>> getPatientsWithoutHealthProfile() {
        log.info("GET /api/users/patients/without-profile - Fetching patients without health profile");
        List<User> patients = userService.getPatientsWithoutHealthProfile();
        List<UserResponse> responses = patients.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/patients/upcoming-appointments")
    public ResponseEntity<List<UserResponse>> getPatientsWithUpcomingAppointments() {
        log.info("GET /api/users/patients/upcoming-appointments - Fetching patients with upcoming appointments");
        List<User> patients = userService.getPatientsWithUpcomingAppointments();
        List<UserResponse> responses = patients.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // =============== Gestion du statut actif ===============

    @PutMapping("/{id}/activate")
    public ResponseEntity<UserResponse> activateUser(@PathVariable Long id) {
        log.info("PUT /api/users/{}/activate - Activating user", id);
        User user = userService.activateUser(id);
        UserResponse response = userMapper.toResponse(user);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<UserResponse> deactivateUser(@PathVariable Long id) {
        log.info("PUT /api/users/{}/deactivate - Deactivating user", id);
        User user = userService.deactivateUser(id);
        UserResponse response = userMapper.toResponse(user);
        return ResponseEntity.ok(response);
    }

    // =============== Vérifications ===============

    @GetMapping("/check-email")
    public ResponseEntity<Boolean> checkEmailExists(@RequestParam String email) {
        log.info("GET /api/users/check-email - Checking if email exists: {}", email);
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/check-email-for-role")
    public ResponseEntity<Boolean> isEmailAvailableForRole(
            @RequestParam String email,
            @RequestParam UserRole role) {
        log.info("GET /api/users/check-email-for-role - Checking email {} for role {}", email, role);
        boolean available = userService.isEmailAvailableForRole(email, role);
        return ResponseEntity.ok(available);
    }

    @GetMapping("/check-license/{licenseNumber}")
    public ResponseEntity<Boolean> checkLicenseExists(@PathVariable String licenseNumber) {
        log.info("GET /api/users/check-license/{} - Checking if license number exists", licenseNumber);
        boolean exists = userService.existsByLicenseNumber(licenseNumber);
        return ResponseEntity.ok(exists);
    }

    // =============== Statistiques avancées ===============

    @GetMapping("/recent")
    public ResponseEntity<List<UserResponse>> getRecentlyRegistered(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {
        log.info("GET /api/users/recent - Fetching users registered since: {}", since);
        List<User> users = userService.getRecentlyRegistered(since);
        List<UserResponse> responses = users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/advanced-search")
    public ResponseEntity<List<UserResponse>> advancedSearch(
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) UserRole role,
            @RequestParam(required = false) Boolean active) {
        log.info("GET /api/users/advanced-search - Advanced search with criteria");
        List<User> users = userService.advancedSearch(firstName, lastName, email, role, active);
        List<UserResponse> responses = users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}