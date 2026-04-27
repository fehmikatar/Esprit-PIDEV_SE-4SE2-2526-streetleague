package tn.esprit._4se2.pi.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.services.Cart.CartStatsService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
@CrossOrigin("*") 
public class CartStatsController {

    private final CartStatsService cartStatsService;

    @GetMapping("/top-products")
    public List<Map<String, Object>> getTopSellingProducts() {
        return cartStatsService.getTopSellingProducts();
    }

    @GetMapping("/abandoned-by-city")
    public List<Map<String, Object>> getAbandonedStatsByCity() {
        return cartStatsService.getAbandonedStatsByCity();
    }

    @GetMapping("/promo-codes")
    public List<Map<String, Object>> getPromoCodeStats() {
        return cartStatsService.getPromoCodeStats();
    }
}
