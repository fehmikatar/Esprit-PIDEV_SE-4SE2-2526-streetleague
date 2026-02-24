package tn.esprit._4se2.pi.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.repositories.UserRepository;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        // 1. Chercher l'utilisateur par email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found: " + email));

        // 2. Convertir son rôle en autorité Spring Security
        var authority = new SimpleGrantedAuthority(user.getRole().name());

        // 3. Retourner l'objet UserDetails
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(List.of(authority))
                .disabled(!user.getIsActive())
                .build();
    }
}