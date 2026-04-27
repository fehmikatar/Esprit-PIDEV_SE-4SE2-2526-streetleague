package tn.esprit._4se2.pi.services.Product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit._4se2.pi.entities.Category;
import tn.esprit._4se2.pi.entities.Product;
import tn.esprit._4se2.pi.repositories.CategoryRepository;
import tn.esprit._4se2.pi.repositories.ProductRepository;
import tn.esprit._4se2.pi.services.CSVReader;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProductImportService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public int importSportsProductsFromCSV() throws Exception {
        String filePath = "src/main/resources/data/shein_sports_products.csv";
        int importedCount = 0;

        // Vérifier si le fichier existe
        if (!Files.exists(Paths.get(filePath))) {
            System.err.println("❌ Fichier non trouvé : " + filePath);
            return 0;
        }

        try (CSVReader reader = new CSVReader(Files.newBufferedReader(Paths.get(filePath)))) {
            String[] header = reader.readNext(); // Skip header
            System.out.println("📋 Header trouvé");

            String[] line;

            while ((line = reader.readNext()) != null) {
                try {
                    if (line.length < 28) {
                        continue;
                    }

                    String productName = line[0];
                    String description = line[1];
                    BigDecimal price = new BigDecimal(line[3]);
                    String sportCategory = line[27];

                    // Éviter les doublons
                    if (productRepository.existsByNomAndDeletedFalse(productName)) {
                        continue;
                    }

                    // Créer ou récupérer la catégorie
                    Category category = categoryRepository.findByNomIgnoreCase(sportCategory)
                            .orElseGet(() -> {
                                Category newCat = new Category();
                                newCat.setNom(sportCategory);
                                newCat.setDescription("Produits de " + sportCategory);
                                newCat.setCreatedAt(LocalDateTime.now());
                                return categoryRepository.save(newCat);
                            });

                    // Créer le produit
                    Product product = new Product();
                    product.setNom(productName.length() > 255 ? productName.substring(0, 255) : productName);
                    product.setDescription(description.length() > 1000 ? description.substring(0, 1000) : description);
                    product.setPrix(price);
                    product.setStock(100);
                    product.setCategory(category);
                    product.setStatus(Product.ProductStatus.EN_STOCK);
                    product.setDeleted(false);
                    product.setCreatedAt(LocalDateTime.now());
                    product.setUpdatedAt(LocalDateTime.now());

                    productRepository.save(product);
                    importedCount++;

                } catch (Exception e) {
                    System.err.println("❌ Erreur sur un produit: " + e.getMessage());
                }
            }
        }

        System.out.println("✅ Import terminé : " + importedCount + " produits");
        return importedCount;
    }
}