package tn.esprit._4se2.pi.services.PricingRule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.dto.PricingRuleRequest;
import tn.esprit._4se2.pi.dto.PricingRuleResponse;
import tn.esprit._4se2.pi.entities.PricingRule;
import tn.esprit._4se2.pi.mappers.PricingRuleMapper;
import tn.esprit._4se2.pi.repositories.PricingRuleRepository;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PricingRuleService implements IPricingRuleService {

    private final PricingRuleRepository pricingRuleRepository;
    private final PricingRuleMapper pricingRuleMapper;

    @Override
    public PricingRuleResponse createPricingRule(PricingRuleRequest request) {
        log.info("Creating pricing rule: {} for sport space: {}", request.getRuleName(), request.getSportSpaceId());

        PricingRule pricingRule = pricingRuleMapper.toEntity(request);
        pricingRule.setSportSpaceId(request.getSportSpaceId());

        PricingRule savedPricingRule = pricingRuleRepository.save(pricingRule);
        log.info("Pricing rule created successfully with id: {}", savedPricingRule.getId());

        return pricingRuleMapper.toResponse(savedPricingRule);
    }

    @Override
    @Transactional(readOnly = true)
    public PricingRuleResponse getPricingRuleById(Long id) {
        log.info("Fetching pricing rule with id: {}", id);
        return pricingRuleRepository.findById(id)
                .map(pricingRuleMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Pricing rule not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PricingRuleResponse> getAllPricingRules() {
        log.info("Fetching all pricing rules");
        return pricingRuleRepository.findAll()
                .stream()
                .map(pricingRuleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PricingRuleResponse> getPricingRulesBySportSpaceId(Long sportSpaceId) {
        log.info("Fetching pricing rules for sport space: {}", sportSpaceId);
        return pricingRuleRepository.findBySportSpaceId(sportSpaceId)
                .stream()
                .map(pricingRuleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PricingRuleResponse> getActivePricingRules() {
        log.info("Fetching active pricing rules");
        return pricingRuleRepository.findByIsActiveTrue()
                .stream()
                .map(pricingRuleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PricingRuleResponse> getPricingRulesByType(String ruleType) {
        log.info("Fetching pricing rules by type: {}", ruleType);
        return pricingRuleRepository.findByRuleType(ruleType)
                .stream()
                .map(pricingRuleMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PricingRuleResponse updatePricingRule(Long id, PricingRuleRequest request) {
        log.info("Updating pricing rule with id: {}", id);

        PricingRule pricingRule = pricingRuleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pricing rule not found with id: " + id));

        pricingRuleMapper.updateEntity(request, pricingRule);
        PricingRule updatedPricingRule = pricingRuleRepository.save(pricingRule);
        log.info("Pricing rule updated successfully with id: {}", id);

        return pricingRuleMapper.toResponse(updatedPricingRule);
    }

    @Override
    public void deletePricingRule(Long id) {
        log.info("Deleting pricing rule with id: {}", id);

        if (!pricingRuleRepository.existsById(id)) {
            throw new RuntimeException("Pricing rule not found with id: " + id);
        }

        pricingRuleRepository.deleteById(id);
        log.info("Pricing rule deleted successfully with id: {}", id);
    }
}