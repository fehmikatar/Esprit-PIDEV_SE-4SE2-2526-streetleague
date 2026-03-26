package tn.esprit._4se2.pi.dto.Auth;

import tn.esprit._4se2.pi.Enum.Role;

public record RegisterRequest(
        String firstName,
        String lastName,
        String email,
        String password,
        Role role
) {}