package tn.esprit._4se2.pi.dto.Auth;

public record AuthResponse(
        Long id,
        String token,
        String email,
        String name,
        String role
) {}