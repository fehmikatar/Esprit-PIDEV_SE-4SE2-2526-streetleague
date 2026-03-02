package tn.esprit._4se2.pi.services.Auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tn.esprit._4se2.pi.Enum.Role;
import tn.esprit._4se2.pi.dto.*;
import tn.esprit._4se2.pi.entities.*;
import tn.esprit._4se2.pi.repositories.UserRepository;
import tn.esprit._4se2.pi.security.CustomUserDetailsService;
import tn.esprit._4se2.pi.security.jwt.JwtService;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtService jwtService;

    // 📝 Inscription
    public String register(RegisterRequest req) {

        // Validations
        if (req.email() == null || req.email().isBlank())
            throw new IllegalArgumentException("Email requis");

        if (userRepository.findByEmail(req.email()).isPresent())
            throw new IllegalArgumentException("Email déjà utilisé");

        if (req.password() == null || req.password().length() < 6)
            throw new IllegalArgumentException(
                    "Mot de passe trop court (min 6 caractères)");

        if (req.role() == null)
            throw new IllegalArgumentException("Rôle requis");

        // Créer le bon type selon le rôle
        User user = switch (req.role()) {
            case ROLE_ADMIN -> {
                Admin admin = new Admin();
                admin.setFirstName(req.firstName());
                admin.setLastName(req.lastName());
                admin.setEmail(req.email());
                admin.setPasswordHash(
                        passwordEncoder.encode(req.password()));
                admin.setRole(Role.ROLE_ADMIN);
                admin.setIsActive(true);
                admin.setCreatedAt(LocalDateTime.now());
                yield admin;
            }
            case ROLE_FIELD_OWNER -> {
                FieldOwner fo = new FieldOwner();
                fo.setFirstName(req.firstName());
                fo.setLastName(req.lastName());
                fo.setEmail(req.email());
                fo.setPasswordHash(
                        passwordEncoder.encode(req.password()));
                fo.setRole(Role.ROLE_FIELD_OWNER);
                fo.setIsActive(true);
                fo.setCreatedAt(LocalDateTime.now());
                yield fo;
            }
            case ROLE_PLAYER -> {
                Player player = new Player();
                player.setFirstName(req.firstName());
                player.setLastName(req.lastName());
                player.setEmail(req.email());
                player.setPasswordHash(
                        passwordEncoder.encode(req.password()));
                player.setRole(Role.ROLE_PLAYER);
                player.setIsActive(true);
                player.setCreatedAt(LocalDateTime.now());
                yield player;
            }
        };

        userRepository.save(user);
        return "Utilisateur créé : " + user.getEmail();
    }

    // 🔐 Connexion
    public AuthResponse login(LoginRequest req) {

        // 1. Vérifier email + mot de passe
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.email(), req.password())
        );

        // 2. Charger les détails
        UserDetails userDetails =
                userDetailsService.loadUserByUsername(req.email());

        // 3. Générer le JWT
        String token = jwtService.generateToken(userDetails);

        // 4. Récupérer le rôle
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse("UNKNOWN");

        return new AuthResponse(token, req.email(), role);
    }
}