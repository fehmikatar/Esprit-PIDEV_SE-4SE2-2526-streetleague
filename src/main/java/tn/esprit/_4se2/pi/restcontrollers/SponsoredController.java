package tn.esprit._4se2.pi.restcontrollers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.dto.Sponsor.ProductDTOs;
import tn.esprit._4se2.pi.entities.Product;
import tn.esprit._4se2.pi.repositories.FavoriteRepository;
import tn.esprit._4se2.pi.repositories.ProductRepository;
import tn.esprit._4se2.pi.services.Sponsor.FlaskMLService;
import tn.esprit._4se2.pi.services.Sponsor.SponsoredRecommendationService;
import tn.esprit._4se2.pi.services.Sponsor.SponsoredTrackingService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sponsored")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = " Sponsored AI", description = "IA de recommandation sponsorisée")
@SecurityRequirement(name = "bearerAuth")
public class SponsoredController {

    private final SponsoredRecommendationService recommendationService;
    private final SponsoredTrackingService trackingService;
    private final ProductRepository productRepository;
    private final FlaskMLService flaskMLService;
    private final FavoriteRepository favoriteRepository;

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



    @GetMapping("/recommendations/ai")
    @Operation(summary = " Recommandations IA - Classement par catégorie, taille et favoris")
    public ResponseEntity<Map<String, Object>> getAIRecommendations(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "10") int limit) {

        List<Map<String, Object>> ranked = flaskMLService.getAIRecommendations(userId, limit);

        List<Long> favProductIds = favoriteRepository.findProductIdsByUserId(userId);
        List<Long> favCategoryIds = favoriteRepository.findFavoriteCategoryIdsByUserId(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("user_id", userId);
        response.put("total", ranked.size());
        response.put("flask_available", flaskMLService.isFlaskApiAvailable());
        response.put("debug_favorite_products", favProductIds);
        response.put("debug_preferred_categories", favCategoryIds);
        response.put("ranked_products", ranked);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/debug")
    @Operation(summary = "Debug - Voir les données de l'utilisateur pour l'IA")
    public ResponseEntity<Map<String, Object>> debugUserData(@RequestParam Long userId) {
        List<Long> favProductIds = favoriteRepository.findProductIdsByUserId(userId);
        List<Long> favCategoryIds = favoriteRepository.findFavoriteCategoryIdsByUserId(userId);
        List<Product> allProducts = productRepository.findByDeletedFalse();

        List<Map<String, Object>> productDetails = new ArrayList<>();
        for (Product p : allProducts) {
            Long catId = p.getCategory() != null ? p.getCategory().getId() : 0L;
            boolean isFav = favProductIds.contains(p.getId());
            boolean isPrefCat = favCategoryIds.contains(catId);
            long freq = favoriteRepository.countFavoritesByUserAndProductCategory(userId, catId);

            Map<String, Object> detail = new HashMap<>();
            detail.put("product_id", p.getId());
            detail.put("product_name", p.getNom());
            detail.put("category_id", catId);
            detail.put("is_favorite", isFav);
            detail.put("is_preferred_category", isPrefCat);
            detail.put("search_frequency", freq);
            productDetails.add(detail);
        }

        Map<String, Object> debug = new HashMap<>();
        debug.put("user_id", userId);
        debug.put("favorite_product_ids", favProductIds);
        debug.put("preferred_category_ids", favCategoryIds);
        debug.put("product_count", allProducts.size());
        debug.put("products", productDetails);
        return ResponseEntity.ok(debug);
    }

    @GetMapping("/predict-score")
    @Operation(summary = "Prédire le score de recommandation pour un produit")
    public ResponseEntity<Map<String, Object>> predictScore(
            @RequestParam Long userId,
            @RequestParam Long productId) {

        Double score = flaskMLService.predictScore(userId, productId);

        Map<String, Object> response = new HashMap<>();
        response.put("user_id", userId);
        response.put("product_id", productId);
        response.put("recommendation_score", score);
        response.put("priority", score > 70 ? "HIGH" : score > 50 ? "MEDIUM" : "LOW");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/model-info")
    @Operation(summary = "Informations sur le modèle IA Flask")
    public ResponseEntity<Map<String, Object>> getModelInfo() {
        return ResponseEntity.ok(flaskMLService.getModelInfo());
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
