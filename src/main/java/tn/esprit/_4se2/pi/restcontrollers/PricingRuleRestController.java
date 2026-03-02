package tn.esprit._4se2.pi.restcontrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.dto.PricingRuleRequest;
import tn.esprit._4se2.pi.dto.PricingRuleResponse;
import tn.esprit._4se2.pi.services.PricingRule.IPricingRuleService;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/pricing-rules")
@RequiredArgsConstructor
public class PricingRuleRestController {

    private final IPricingRuleService pricingRuleService;

    @PostMapping
    public ResponseEntity<PricingRuleResponse> createPricingRule(@Valid @RequestBody PricingRuleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pricingRuleService.createPricingRule(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PricingRuleResponse> getPricingRuleById(@PathVariable Long id) {
        return ResponseEntity.ok(pricingRuleService.getPricingRuleById(id));
    }

    @GetMapping
    public ResponseEntity<List<PricingRuleResponse>> getAllPricingRules() {
        return ResponseEntity.ok(pricingRuleService.getAllPricingRules());
    }

    @GetMapping("/sport-space/{sportSpaceId}")
    public ResponseEntity<List<PricingRuleResponse>> getPricingRulesBySportSpaceId(@PathVariable Long sportSpaceId) {
        return ResponseEntity.ok(pricingRuleService.getPricingRulesBySportSpaceId(sportSpaceId));
    }

    @GetMapping("/active")
    public ResponseEntity<List<PricingRuleResponse>> getActivePricingRules() {
        return ResponseEntity.ok(pricingRuleService.getActivePricingRules());
    }

    @GetMapping("/type/{ruleType}")
    public ResponseEntity<List<PricingRuleResponse>> getPricingRulesByType(@PathVariable String ruleType) {
        return ResponseEntity.ok(pricingRuleService.getPricingRulesByType(ruleType));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PricingRuleResponse> updatePricingRule(
            @PathVariable Long id,
            @Valid @RequestBody PricingRuleRequest request) {
        return ResponseEntity.ok(pricingRuleService.updatePricingRule(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePricingRule(@PathVariable Long id) {
        pricingRuleService.deletePricingRule(id);
        return ResponseEntity.noContent().build();
    }
}