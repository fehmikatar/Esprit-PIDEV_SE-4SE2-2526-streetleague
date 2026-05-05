package tn.esprit._4se2.pi.restcontrollers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.dto.Loyalty.AddPointsRequest;
import tn.esprit._4se2.pi.dto.Loyalty.LoyaltyTransactionResponse;
import tn.esprit._4se2.pi.services.Loyalty.ILoyaltyService;

import java.util.List;

@RestController
@RequestMapping("/api/loyalty/transactions")
@RequiredArgsConstructor
public class LoyaltyTransactionController {

    private final ILoyaltyService loyaltyService;

    @PostMapping("/add-points")
    public void addPoints(@Valid @RequestBody AddPointsRequest request) {
        loyaltyService.addPoints(request);
    }

    @PostMapping("/redeem")
    public void redeemPoints(@RequestParam Long userId, @RequestParam Integer points, @RequestParam String reason) {
        loyaltyService.redeemPoints(userId, points, reason);
    }

    @GetMapping("/user/{userId}")
    public List<LoyaltyTransactionResponse> getUserTransactions(@PathVariable Long userId) {
        return loyaltyService.getUserTransactions(userId);
    }
}