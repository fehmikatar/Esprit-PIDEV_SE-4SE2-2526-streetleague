package tn.esprit._4se2.pi.dto.Auth;

public record LoginRequest(
        String email,
        String password
) {}