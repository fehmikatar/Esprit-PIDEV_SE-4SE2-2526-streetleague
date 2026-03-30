package tn.esprit._4se2.pi.dto.User;

import lombok.*;
import tn.esprit._4se2.pi.entities.UserRole;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String passwordHash;
    private UserRole role;
    private String specialty;
    private String licenseNumber;
    private String address;
}