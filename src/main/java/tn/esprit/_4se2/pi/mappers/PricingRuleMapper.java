package tn.esprit._4se2.pi.mappers;

import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.dto.PricingRuleRequest;
import tn.esprit._4se2.pi.dto.PricingRuleResponse;
import tn.esprit._4se2.pi.entities.PricingRule;
import java.time.LocalDateTime;

@Component
public class PricingRuleMapper {

    public PricingRule toEntity(PricingRuleRequest request) {
        if (request == null) return null;

        PricingRule pricingRule = new PricingRule();
        pricingRule.setRuleName(request.getRuleName());
        pricingRule.setRuleType(request.getRuleType());
        pricingRule.setBasePrice(request.getBasePrice());
        pricingRule.setDiscountPercentage(request.getDiscountPercentage());
        pricingRule.setMinBookingHours(request.getMinBookingHours());
        pricingRule.setMaxBookingHours(request.getMaxBookingHours());
        pricingRule.setIsActive(request.getIsActive());
        pricingRule.setEffectiveFrom(request.getEffectiveFrom());
        pricingRule.setEffectiveTo(request.getEffectiveTo());
        return pricingRule;
    }

    public PricingRuleResponse toResponse(PricingRule entity) {
        if (entity == null) return null;

        return PricingRuleResponse.builder()
                .id(entity.getId())
                .sportSpaceId(entity.getSportSpaceId())
                .ruleName(entity.getRuleName())
                .ruleType(entity.getRuleType())
                .basePrice(entity.getBasePrice())
                .discountPercentage(entity.getDiscountPercentage())
                .minBookingHours(entity.getMinBookingHours())
                .maxBookingHours(entity.getMaxBookingHours())
                .isActive(entity.getIsActive())
                .effectiveFrom(entity.getEffectiveFrom())
                .effectiveTo(entity.getEffectiveTo())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public void updateEntity(PricingRuleRequest request, PricingRule pricingRule) {
        if (request == null || pricingRule == null) return;

        pricingRule.setRuleName(request.getRuleName());
        pricingRule.setRuleType(request.getRuleType());
        pricingRule.setBasePrice(request.getBasePrice());
        pricingRule.setDiscountPercentage(request.getDiscountPercentage());
        pricingRule.setMinBookingHours(request.getMinBookingHours());
        pricingRule.setMaxBookingHours(request.getMaxBookingHours());
        pricingRule.setIsActive(request.getIsActive());
        pricingRule.setEffectiveFrom(request.getEffectiveFrom());
        pricingRule.setEffectiveTo(request.getEffectiveTo());
    }
}