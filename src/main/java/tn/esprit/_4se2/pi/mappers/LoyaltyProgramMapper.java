package tn.esprit._4se2.pi.mappers;

import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.dto.Loyalty.LoyaltyProgramRequest;
import tn.esprit._4se2.pi.dto.Loyalty.LoyaltyProgramResponse;
import tn.esprit._4se2.pi.entities.LoyaltyProgram;

@Component
public class LoyaltyProgramMapper {

    public LoyaltyProgram toEntity(LoyaltyProgramRequest request) {
        return LoyaltyProgram.builder()
                .name(request.getName())
                .description(request.getDescription())
                .pointsPerCurrencyUnit(request.getPointsPerCurrencyUnit())
                .active(true)
                .build();
    }

    public LoyaltyProgramResponse toResponse(LoyaltyProgram program) {
        return LoyaltyProgramResponse.builder()
                .id(program.getId())
                .name(program.getName())
                .description(program.getDescription())
                .active(program.isActive())
                .pointsPerCurrencyUnit(program.getPointsPerCurrencyUnit())
                .createdAt(program.getCreatedAt())
                .updatedAt(program.getUpdatedAt())
                .build();
    }
}