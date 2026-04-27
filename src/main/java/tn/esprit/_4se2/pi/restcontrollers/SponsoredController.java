package tn.esprit._4se2.pi.restcontrollers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.dto.Sponsor.ProductDTOs;
import tn.esprit._4se2.pi.entities.Product;
import tn.esprit._4se2.pi.repositories.ProductRepository;
import tn.esprit._4se2.pi.services.Sponsor.SponsoredRecommendationService;
import tn.esprit._4se2.pi.services.Sponsor.SponsoredTrackingService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sponsored")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "🤖 Sponsored AI", description = "IA de recommandation sponsorisée")
@SecurityRequirement(name = "bearerAuth")
public class SponsoredController {

    private final SponsoredRecommendationService recommendationService;
    private final SponsoredTrackingService trackingService;
    private final ProductRepository productRepository;

    @GetMapping("/recommendations")
    @Operation(summary = "Obtenir les recommandations sponsorisées")
    public ResponseEntity<List<ProductDTOs.ProductResponse>> getRecommendations(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(recommendationService.getSponsoredRecommendations(userId, limit));
    }

    @GetMapping("/recommendations/advanced")
    @Operation(summary = "Obtenir les recommandations avancées (IA SVD)")
    public ResponseEntity<List<ProductDTOs.ProductResponse>> getAdvancedRecommendations(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(recommendationService.getAdvancedRecommendations(userId, limit));
    }

    @PostMapping("/impression")
    @Operation(summary = "Enregistrer une impression sponsorisée")
    public ResponseEntity<Map<String, Object>> recordImpression(
            @RequestParam(required = false) Long userId,
            @RequestParam Long productId,
            @RequestParam(required = false) Integer position,
            @RequestParam(required = false) String sessionId,
            @RequestParam(required = false) Double bidAmount) {

        Product product = productRepository.findById(productId).orElse(null);
        double relevanceScore = recommendationService.calculateRelevanceScore(userId, product);

        var click = trackingService.recordImpression(userId, productId, position, sessionId, bidAmount, relevanceScore);

        Map<String, Object> response = new HashMap<>();
        response.put("success", click != null);
        response.put("clickId", click != null ? click.getId() : null);
        response.put("relevanceScore", relevanceScore);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/click/{clickId}")
    @Operation(summary = "Enregistrer un clic sur produit sponsorisé")
    public ResponseEntity<Map<String, Object>> recordClick(
            @PathVariable Long clickId,
            @RequestParam(required = false) Integer duration) {

        var click = trackingService.recordClick(clickId, duration);

        Map<String, Object> response = new HashMap<>();
        response.put("success", click != null);
        response.put("message", click != null ? "Clic enregistré" : "Erreur");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/purchase/{clickId}")
    @Operation(summary = "Enregistrer un achat suite à un clic sponsorisé")
    public ResponseEntity<Map<String, Object>> recordPurchase(@PathVariable Long clickId) {
        var click = trackingService.recordPurchase(clickId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", click != null);
        response.put("message", click != null ? "Achat enregistré" : "Erreur");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stats")
    @Operation(summary = "Statistiques du système sponsorisé")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(trackingService.getStats());
    }
}