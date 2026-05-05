package tn.esprit._4se2.pi.restcontrollers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.dto.Loyalty.LoyaltyTierRequest;
import tn.esprit._4se2.pi.dto.Loyalty.LoyaltyTierResponse;
import tn.esprit._4se2.pi.services.Loyalty.ILoyaltyService;

import java.util.List;

@RestController
@RequestMapping("/api/loyalty/tiers")
@RequiredArgsConstructor
public class LoyaltyTierController {

    private final ILoyaltyService loyaltyService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoyaltyTierResponse create(@Valid @RequestBody LoyaltyTierRequest request) {
        return loyaltyService.createTier(request);
    }

    @GetMapping("/program/{programId}")
    public List<LoyaltyTierResponse> getByProgram(@PathVariable Long programId) {
        return loyaltyService.getAllTiersByProgram(programId);
    }

    @GetMapping("/{id}")
    public LoyaltyTierResponse getById(@PathVariable Long id) {
        return loyaltyService.getTierById(id);
    }

    @PutMapping("/{id}")
    public LoyaltyTierResponse update(@PathVariable Long id, @Valid @RequestBody LoyaltyTierRequest request) {
        return loyaltyService.updateTier(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        loyaltyService.deleteTier(id);
    }
}