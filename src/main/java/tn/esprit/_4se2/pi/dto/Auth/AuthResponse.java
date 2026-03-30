package tn.esprit._4se2.pi.dto.Auth;

public record AuthResponse(
        String token,
        String email,
        String role
) {}