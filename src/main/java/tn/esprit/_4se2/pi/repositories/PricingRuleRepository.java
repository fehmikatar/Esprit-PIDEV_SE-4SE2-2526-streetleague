package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.entities.PricingRule;
import java.util.List;

@Repository
public interface PricingRuleRepository extends JpaRepository<PricingRule, Long> {
    List<PricingRule> findBySportSpaceId(Long sportSpaceId);
    List<PricingRule> findByIsActiveTrue();
    List<PricingRule> findByRuleType(String ruleType);
}