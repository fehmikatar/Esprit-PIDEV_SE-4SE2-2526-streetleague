package tn.esprit._4se2.pi.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.entities.Promotion;
import tn.esprit._4se2.pi.services.PromotionService;

import java.util.List;

@RestController
@RequestMapping("/api/promotions")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionService promotionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Promotion createPromotion(@RequestBody Promotion promotion) {
        return promotionService.createPromotion(promotion);
    }

    @GetMapping
    public List<Promotion> getAllPromotions() {
        return promotionService.getAllPromotions();
    }

    @GetMapping("/{id}")
    public Promotion getPromotionById(@PathVariable Long id) {
        return promotionService.getPromotionById(id);
    }

    @PutMapping("/{id}")
    public Promotion updatePromotion(@PathVariable Long id, @RequestBody Promotion promotion) {
        return promotionService.updatePromotion(id, promotion);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePromotion(@PathVariable Long id) {
        promotionService.deletePromotion(id);
    }
}