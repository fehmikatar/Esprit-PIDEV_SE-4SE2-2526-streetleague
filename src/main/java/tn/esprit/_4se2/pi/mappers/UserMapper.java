package tn.esprit._4se2.pi.mappers;

import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.dto.UserRequest;
import tn.esprit._4se2.pi.dto.UserResponse;
import tn.esprit._4se2.pi.entities.User;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserMapper {

    public User toEntity(UserRequest request) {
        if (request == null) return null;
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPasswordHash(request.getPasswordHash());
        user.setRole(request.getRole());
        user.setSpecialty(request.getSpecialty());
        user.setLicenseNumber(request.getLicenseNumber());
        user.setAddress(request.getAddress());
        return user;
    }

    public UserResponse toResponse(User user) {
        if (user == null) return null;
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .passwordHash(user.getPasswordHash())
                .createdAt(user.getCreatedAt())
                .active(user.isActive())
                .role(user.getRole())
                .specialty(user.getSpecialty())
                .licenseNumber(user.getLicenseNumber())
                .address(user.getAddress())
                .build();
    }
}