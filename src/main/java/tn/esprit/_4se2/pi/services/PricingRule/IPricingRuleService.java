package tn.esprit._4se2.pi.services.PricingRule;

import tn.esprit._4se2.pi.dto.PricingRuleRequest;
import tn.esprit._4se2.pi.dto.PricingRuleResponse;
import java.util.List;

public interface IPricingRuleService {
    PricingRuleResponse createPricingRule(PricingRuleRequest request);
    PricingRuleResponse getPricingRuleById(Long id);
    List<PricingRuleResponse> getAllPricingRules();
    List<PricingRuleResponse> getPricingRulesBySportSpaceId(Long sportSpaceId);
    List<PricingRuleResponse> getActivePricingRules();
    List<PricingRuleResponse> getPricingRulesByType(String ruleType);
    PricingRuleResponse updatePricingRule(Long id, PricingRuleRequest request);
    void deletePricingRule(Long id);
}