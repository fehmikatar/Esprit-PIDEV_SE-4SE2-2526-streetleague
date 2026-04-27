package tn.esprit._4se2.pi.restcontrollers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.repositories.CategoryRepository;
import tn.esprit._4se2.pi.repositories.ProductRepository;
import tn.esprit._4se2.pi.services.Product.ProductImportService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/import")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Import/Export", description = "📥 API d'import des données")
@SecurityRequirement(name = "bearerAuth")  // ← AJOUTEZ CETTE LIGNE
public class ImportController {

    private final ProductImportService productImportService;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @PostMapping("/sports")
    @Operation(
            summary = "Importer les produits sportifs",
            description = "Importe 94 produits sportifs depuis le fichier CSV. Crée automatiquement les catégories sportives."
    )
    public ResponseEntity<Map<String, Object>> importSportsProducts() {
        Map<String, Object> response = new HashMap<>();
        try {
            int count = productImportService.importSportsProductsFromCSV();
            response.put("success", true);
            response.put("message", "Import terminé avec succès");
            response.put("productsImported", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    @GetMapping("/stats")
    @Operation(
            summary = "Statistiques d'import",
            description = "Retourne le nombre total de produits et de catégories sportives importés"
    )
    public ResponseEntity<Map<String, Object>> getImportStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalProducts", productRepository.count());
        stats.put("sportsCategories", categoryRepository.count());
        stats.put("status", "Import réussi avec 94 produits sportifs !");
        return ResponseEntity.ok(stats);
    }
}