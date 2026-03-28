package tn.esprit._4se2.pi.dto;

import lombok.*;
import tn.esprit._4se2.pi.entites.UserRole;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String passwordHash;
    private LocalDateTime createdAt;
    private boolean active;
    private UserRole role;
    private String specialty;
    private String licenseNumber;
    private String address;
}
