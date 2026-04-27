package tn.esprit._4se2.pi.restcontrollers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.repositories.*;
import tn.esprit._4se2.pi.services.Sponsor.BiddingService;
import tn.esprit._4se2.pi.services.Sponsor.ModelTrainingService;
import tn.esprit._4se2.pi.services.Sponsor.SponsoredTrackingService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Admin Dashboard", description = "📊 Tableau de bord administrateur")
@SecurityRequirement(name = "bearerAuth")
public class AdminDashboardController {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final SponsoredClickRepository clickRepository;
    private final CategoryRepository categoryRepository;
    private final SponsoredTrackingService trackingService;
    private final ModelTrainingService modelTrainingService;
    private final BiddingService biddingService;

    @GetMapping("/stats")
    @Operation(summary = "Statistiques globales")
    public ResponseEntity<Map<String, Object>> getGlobalStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalProducts", productRepository.count());
        stats.put("activeProducts", productRepository.findByDeletedFalse().size());
        stats.put("totalUsers", userRepository.count());
        stats.put("totalCategories", categoryRepository.count());
        stats.put("sponsoredStats", trackingService.getStats());
        stats.put("modelStats", modelTrainingService.getModelStats());

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/revenue")
    @Operation(summary = "Statistiques de revenus")
    public ResponseEntity<Map<String, Object>> getRevenueStats() {
        Map<String, Object> revenue = new HashMap<>();

        double totalSponsoredRevenue = 0.0;
        long totalClicks = 0;

        for (var click : clickRepository.findAll()) {
            if (click.getIsPurchased() != null && click.getIsPurchased() && click.getBidAmount() != null) {
                totalSponsoredRevenue += click.getBidAmount();
            }
            if (click.getIsClicked() != null && click.getIsClicked()) {
                totalClicks++;
            }
        }

        revenue.put("totalSponsoredRevenue", totalSponsoredRevenue);
        revenue.put("totalClicks", totalClicks);

        long totalImpressions = clickRepository.count();
        double globalCTR = totalImpressions > 0 ? (double) totalClicks / totalImpressions * 100 : 0;
        revenue.put("globalCTR", Math.round(globalCTR * 100.0) / 100.0 + "%");

        return ResponseEntity.ok(revenue);
    }

    @PostMapping("/train-model")
    @Operation(summary = "Forcer l'entraînement du modèle IA")
    public ResponseEntity<Map<String, Object>> forceModelTraining() {
        Map<String, Object> response = new HashMap<>();
        try {
            modelTrainingService.trainModel();
            response.put("success", true);
            response.put("message", "Modèle entraîné avec succès");
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-daily-counters")
    @Operation(summary = "Réinitialiser les compteurs quotidiens")
    public ResponseEntity<Map<String, Object>> resetDailyCounters() {
        Map<String, Object> response = new HashMap<>();
        try {
            biddingService.resetDailyCounters();
            response.put("success", true);
            response.put("message", "Compteurs quotidiens réinitialisés");
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return ResponseEntity.ok(response);
    }
}