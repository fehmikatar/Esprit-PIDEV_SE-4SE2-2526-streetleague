package tn.esprit._4se2.pi.mappers;

import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.dto.UserRequest;
import tn.esprit._4se2.pi.dto.UserResponse;
import tn.esprit._4se2.pi.entities.User;
import java.time.LocalDateTime;

@Component
public class UserMapper {

    public User toEntity(UserRequest request) {
        if (request == null) return null;

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPasswordHash(request.getPassword());
        user.setCreatedAt(LocalDateTime.now());
        user.setIsActive(true);
        return user;
    }

    public UserResponse toResponse(User entity) {
        if (entity == null) return null;

        return UserResponse.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .createdAt(entity.getCreatedAt())
                .isActive(entity.getIsActive())
                .build();
    }

    public void updateEntity(UserRequest request, User user) {
        if (request == null || user == null) return;

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
    }
}