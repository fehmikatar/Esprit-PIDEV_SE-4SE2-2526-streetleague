package tn.esprit._4se2.pi.mappers;

import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.dto.FieldOwnerRequest;
import tn.esprit._4se2.pi.dto.FieldOwnerResponse;
import tn.esprit._4se2.pi.entities.FieldOwner;
import java.time.LocalDateTime;

@Component
public class FieldOwnerMapper {

    public FieldOwner toEntity(FieldOwnerRequest request) {
        if (request == null) return null;

        FieldOwner fieldOwner = new FieldOwner();
        fieldOwner.setFirstName(request.getFirstName());
        fieldOwner.setLastName(request.getLastName());
        fieldOwner.setEmail(request.getEmail());
        fieldOwner.setPhone(request.getPhone());
        fieldOwner.setPasswordHash(request.getPassword());
        fieldOwner.setBusinessName(request.getBusinessName());
        fieldOwner.setBusinessLicense(request.getBusinessLicense());
        fieldOwner.setLocation(request.getLocation());
        fieldOwner.setTotalRevenue(request.getTotalRevenue() != null ? request.getTotalRevenue() : 0.0);
        fieldOwner.setCreatedAt(LocalDateTime.now());
        fieldOwner.setIsActive(true);
        return fieldOwner;
    }

    public FieldOwnerResponse toResponse(FieldOwner entity) {
        if (entity == null) return null;

        return FieldOwnerResponse.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .businessName(entity.getBusinessName())
                .businessLicense(entity.getBusinessLicense())
                .location(entity.getLocation())
                .totalRevenue(entity.getTotalRevenue())
                .createdAt(entity.getCreatedAt())
                .isActive(entity.getIsActive())
                .build();
    }

    public void updateEntity(FieldOwnerRequest request, FieldOwner fieldOwner) {
        if (request == null || fieldOwner == null) return;

        fieldOwner.setFirstName(request.getFirstName());
        fieldOwner.setLastName(request.getLastName());
        fieldOwner.setEmail(request.getEmail());
        fieldOwner.setPhone(request.getPhone());
        fieldOwner.setBusinessName(request.getBusinessName());
        fieldOwner.setBusinessLicense(request.getBusinessLicense());
        fieldOwner.setLocation(request.getLocation());
    }
}