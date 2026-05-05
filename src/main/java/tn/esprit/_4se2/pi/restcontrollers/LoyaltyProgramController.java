package tn.esprit._4se2.pi.restcontrollers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.dto.Loyalty.LoyaltyProgramRequest;
import tn.esprit._4se2.pi.dto.Loyalty.LoyaltyProgramResponse;
import tn.esprit._4se2.pi.services.Loyalty.ILoyaltyService;

import java.util.List;

@RestController
@RequestMapping("/api/loyalty/programs")
@RequiredArgsConstructor
public class LoyaltyProgramController {

    private final ILoyaltyService loyaltyService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LoyaltyProgramResponse create(@Valid @RequestBody LoyaltyProgramRequest request) {
        return loyaltyService.createProgram(request);
    }

    @GetMapping
    public List<LoyaltyProgramResponse> getAll() {
        return loyaltyService.getAllPrograms();
    }

    @GetMapping("/{id}")
    public LoyaltyProgramResponse getById(@PathVariable Long id) {
        return loyaltyService.getProgramById(id);
    }

    @PutMapping("/{id}")
    public LoyaltyProgramResponse update(@PathVariable Long id, @Valid @RequestBody LoyaltyProgramRequest request) {
        return loyaltyService.updateProgram(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        loyaltyService.deleteProgram(id);
    }
}