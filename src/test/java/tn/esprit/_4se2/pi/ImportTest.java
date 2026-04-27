package tn.esprit._4se2.pi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit._4se2.pi.services.Product.ProductImportService;

@SpringBootTest
public class ImportTest {

    @Autowired
    private ProductImportService productImportService;

    @Test
    public void testImport() throws Exception {
        int count = productImportService.importSportsProductsFromCSV();
        System.out.println("✅ " + count + " produits importés en BDD !");
    }
}