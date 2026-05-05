package tn.esprit._4se2.pi.restcontrollers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.dto.Loyalty.*;
import tn.esprit._4se2.pi.services.Loyalty.ILoyaltyService;

import java.util.List;

@RestController
@RequestMapping("/api/loyalty/clients")
@RequiredArgsConstructor
public class LoyaltyClientController {

    private final ILoyaltyService loyaltyService;

    @PostMapping("/enroll")
    public LoyaltyClientResponse enroll(@Valid @RequestBody EnrollRequest request) {
        return loyaltyService.enrollUser(request);
    }

    @GetMapping("/{userId}")
    public LoyaltyClientResponse getByUser(@PathVariable Long userId) {
        return loyaltyService.getUserLoyalty(userId);
    }

    @GetMapping
    public List<LoyaltyClientResponse> getAll() {
        return loyaltyService.getAllClients();
    }
}