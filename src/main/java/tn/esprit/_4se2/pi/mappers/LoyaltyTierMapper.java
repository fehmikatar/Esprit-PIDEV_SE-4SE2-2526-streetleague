package tn.esprit._4se2.pi.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.dto.Loyalty.LoyaltyTierRequest;
import tn.esprit._4se2.pi.dto.Loyalty.LoyaltyTierResponse;
import tn.esprit._4se2.pi.entities.LoyaltyProgram;
import tn.esprit._4se2.pi.entities.LoyaltyTier;
import tn.esprit._4se2.pi.repositories.LoyaltyProgramRepository;
import tn.esprit._4se2.pi.exception.ResourceNotFoundException;

@Component
@RequiredArgsConstructor
public class LoyaltyTierMapper {

    private final LoyaltyProgramRepository programRepository;

    public LoyaltyTier toEntity(LoyaltyTierRequest request) {
        LoyaltyProgram program = programRepository.findById(request.getProgramId())
                .orElseThrow(() -> new ResourceNotFoundException("Program not found with id: " + request.getProgramId()));
        return LoyaltyTier.builder()
                .program(program)
                .name(request.getName())
                .level(request.getLevel())
                .pointsRequired(request.getPointsRequired())
                .extraDiscountRate(request.getExtraDiscountRate())
                .benefits(request.getBenefits())
                .exclusiveAccessTag(request.getExclusiveAccessTag())
                .build();
    }

    public LoyaltyTierResponse toResponse(LoyaltyTier tier) {
        return LoyaltyTierResponse.builder()
                .id(tier.getId())
                .programId(tier.getProgram().getId())
                .programName(tier.getProgram().getName())
                .name(tier.getName())
                .level(tier.getLevel())
                .pointsRequired(tier.getPointsRequired())
                .extraDiscountRate(tier.getExtraDiscountRate())
                .benefits(tier.getBenefits())
                .exclusiveAccessTag(tier.getExclusiveAccessTag())
                .build();
    }

    public void updateEntity(LoyaltyTierRequest request, LoyaltyTier tier) {
        tier.setName(request.getName());
        tier.setLevel(request.getLevel());
        tier.setPointsRequired(request.getPointsRequired());
        tier.setExtraDiscountRate(request.getExtraDiscountRate());
        tier.setBenefits(request.getBenefits());
        tier.setExclusiveAccessTag(request.getExclusiveAccessTag());
        // program ne change pas (sinon gérer séparément)
    }
}