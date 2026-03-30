package tn.esprit._4se2.pi.restcontrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.Enum.Role;
import tn.esprit._4se2.pi.repository.UserRepository;
import tn.esprit._4se2.pi.security.jwt.JwtService;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthRestController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            // Vérifier si l'email existe déjà
            if (userRepository.findByEmail(request.getEmail()).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Email déjà utilisé"));
            }

            // Split fullName into firstName and lastName
            String fullName = request.getFullName() != null ? request.getFullName() : "";
            String[] parts = fullName.split(" ", 2);
            String firstName = parts[0];
            String lastName = parts.length > 1 ? parts[1] : "";

            // Créer le nouvel utilisateur
            User user = User.builder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(request.getEmail())
                    .passwordHash(passwordEncoder.encode(request.getPassword()))
                    .role(Role.valueOf(request.getRole()))
                    .isActive(true)
                    .build();

            userRepository.save(user);

            return ResponseEntity.ok(Map.of(
                    "message", "Utilisateur créé avec succès",
                    "email", user.getEmail(),
                    "role", user.getRole().name()
            ));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Erreur lors de l'inscription: " + e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails);
            User user = userRepository.findByEmail(request.getEmail()).orElseThrow();

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "email", user.getEmail(),
                    "role", user.getRole().name()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", "Identifiants invalides"));
        }
    }
}

// DTOs
class LoginRequest {
    private String email;
    private String password;
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}

class RegisterRequest {
    private String fullName;
    private String email;
    private String password;
    private String role;
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}