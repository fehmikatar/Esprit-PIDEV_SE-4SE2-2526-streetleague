package tn.esprit._4se2.pi.mappers;

import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.dto.AdminRequest;
import tn.esprit._4se2.pi.dto.AdminResponse;
import tn.esprit._4se2.pi.entities.Admin;
import java.time.LocalDateTime;

@Component
public class AdminMapper {

    public Admin toEntity(AdminRequest request) {
        if (request == null) return null;

        Admin admin = new Admin();
        admin.setFirstName(request.getFirstName());
        admin.setLastName(request.getLastName());
        admin.setEmail(request.getEmail());
        admin.setPhone(request.getPhone());
        admin.setPasswordHash(request.getPassword());
        admin.setCreatedAt(LocalDateTime.now());
        admin.setIsActive(true);
        return admin;
    }

    public AdminResponse toResponse(Admin entity) {
        if (entity == null) return null;

        return AdminResponse.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .adminRole(entity.getAdminRole())
                .department(entity.getDepartment())
                .createdAt(entity.getCreatedAt())
                .isActive(entity.getIsActive())
                .build();
    }

    public void updateEntity(AdminRequest request, Admin admin) {
        if (request == null || admin == null) return;

        admin.setFirstName(request.getFirstName());
        admin.setLastName(request.getLastName());
        admin.setEmail(request.getEmail());
        admin.setPhone(request.getPhone());
    }
}