package tn.esprit._4se2.pi.restcontrollers;

import tn.esprit._4se2.pi.dto.PromoCodeDTO;
import tn.esprit._4se2.pi.service.IPromoCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/promocodes")
@RequiredArgsConstructor
@CrossOrigin(origins = {"*"})
public class PromoCodeRestController {

    private final IPromoCodeService promoCodeService;

    @GetMapping
    public ResponseEntity<List<PromoCodeDTO>> getAllPromoCodes() {
        return ResponseEntity.ok(promoCodeService.getAllPromoCodes());
    }

    @GetMapping("/active")
    public ResponseEntity<List<PromoCodeDTO>> getActivePromoCodes() {
        return ResponseEntity.ok(promoCodeService.getAllActivePromoCodes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PromoCodeDTO> getPromoCodeById(@PathVariable Long id) {
        return ResponseEntity.ok(promoCodeService.getPromoCodeById(id));
    }

    @GetMapping("/code/{code}")
    public ResponseEntity<PromoCodeDTO> getPromoCodeByCode(@PathVariable String code) {
        return ResponseEntity.ok(promoCodeService.getPromoCodeByCode(code));
    }

    @GetMapping("/validate/{code}")
    public ResponseEntity<Boolean> validatePromoCode(@PathVariable String code) {
        return ResponseEntity.ok(promoCodeService.isValidPromoCode(code));
    }

    @PostMapping
    public ResponseEntity<PromoCodeDTO> createPromoCode(@Valid @RequestBody PromoCodeDTO promoCodeDTO) {
        return new ResponseEntity<>(promoCodeService.addPromoCode(promoCodeDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PromoCodeDTO> updatePromoCode(
            @PathVariable Long id,
            @Valid @RequestBody PromoCodeDTO promoCodeDTO) {
        return ResponseEntity.ok(promoCodeService.updatePromoCode(id, promoCodeDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePromoCode(@PathVariable Long id) {
        promoCodeService.deletePromoCodeById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/deactivate-expired")
    public ResponseEntity<Void> deactivateExpiredPromoCodes() {
        promoCodeService.deactivateExpiredPromoCodes();
        return ResponseEntity.ok().build();
    }
}
