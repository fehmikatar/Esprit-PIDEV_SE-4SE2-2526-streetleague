package tn.esprit._4se2.pi.restcontrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.dto.Promotion.PromotionResponse;
import tn.esprit._4se2.pi.services.Loyalty.LoyaltyPromotionService;

import java.util.List;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionIntegrationController {

    private final LoyaltyPromotionService loyaltyPromotionService;

    @GetMapping("/user/{userId}")
    public List<PromotionResponse> getPromotionsForUser(@PathVariable Long userId) {
        return loyaltyPromotionService.getPromotionsForUser(userId);
    }
}