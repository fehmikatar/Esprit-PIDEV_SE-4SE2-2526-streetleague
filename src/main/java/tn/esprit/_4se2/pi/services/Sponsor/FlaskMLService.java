package tn.esprit._4se2.pi.services.Sponsor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import tn.esprit._4se2.pi.entities.Product;
import tn.esprit._4se2.pi.repositories.FavoriteRepository;
import tn.esprit._4se2.pi.repositories.ProductRepository;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class FlaskMLService {

    private final ProductRepository productRepository;
    private final FavoriteRepository favoriteRepository;
    private final RestTemplate restTemplate;

    private static final String FLASK_API_URL = "http://localhost:5000";

    public FlaskMLService(ProductRepository productRepository, FavoriteRepository favoriteRepository) {
        this.productRepository = productRepository;
        this.favoriteRepository = favoriteRepository;
        
        // Configuration du timeout pour ne pas bloquer l'application si Flask est éteint
        org.springframework.http.client.SimpleClientHttpRequestFactory factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(2000); // 2 secondes pour se connecter
        factory.setReadTimeout(2000);    // 2 secondes pour lire la réponse
        this.restTemplate = new RestTemplate(factory);
    }


    public boolean isFlaskApiAvailable() {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                    FLASK_API_URL + "/api/health", Map.class);
            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            log.warn("API Flask non disponible: {}", e.getMessage());
            return false;
        }
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAIRecommendations(Long userId, int limit) {
        List<Product> allProducts = productRepository.findByDeletedFalse();
        if (allProducts.isEmpty()) return Collections.emptyList();

        if (!isFlaskApiAvailable()) {
            log.warn("Flask indisponible → fallback local");
            return fallbackRecommendations(userId, allProducts, limit);
        }

        return rankProductsForUser(userId, allProducts, limit);
    }


    @Transactional(readOnly = true)
    public List<Map<String, Object>> rankProductsForUser(Long userId, List<Product> products, int limit) {
        try {
            List<Long> favoriteProductIds = favoriteRepository.findProductIdsByUserId(userId);
            List<Long> favoriteCategoryIds = favoriteRepository.findFavoriteCategoryIdsByUserId(userId);

            log.info("User {} → {} favoris, {} catégories préférées", userId,
                    favoriteProductIds.size(), favoriteCategoryIds.size());

            List<Map<String, Object>> productFeatures = new ArrayList<>();
            for (Product p : products) {
                Map<String, Object> features = buildFeatures(userId, p, favoriteProductIds, favoriteCategoryIds);
                productFeatures.add(features);
            }
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("user_id", userId);
            requestBody.put("products", productFeatures);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    FLASK_API_URL + "/api/rank-products", request, Map.class);

            if (response.getBody() != null) {
                List<Map<String, Object>> ranked = (List<Map<String, Object>>) response.getBody().get("ranked_products");
                if (ranked != null && ranked.size() > limit) {
                    ranked = ranked.subList(0, limit);
                }
                return ranked != null ? ranked : Collections.emptyList();
            }
        } catch (Exception e) {
            log.error("Erreur appel Flask rank-products: {}", e.getMessage());
        }
        return fallbackRecommendations(userId, products, limit);
    }


    @Transactional(readOnly = true)
    public Double predictScore(Long userId, Long productId) {
        try {
            Product product = productRepository.findById(productId).orElse(null);
            if (product == null) return 0.0;

            List<Long> favoriteProductIds = favoriteRepository.findProductIdsByUserId(userId);
            List<Long> favoriteCategoryIds = favoriteRepository.findFavoriteCategoryIdsByUserId(userId);

            Map<String, Object> features = buildFeatures(userId, product, favoriteProductIds, favoriteCategoryIds);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(features, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    FLASK_API_URL + "/api/predict", request, Map.class);

            if (response.getBody() != null) {
                Object score = response.getBody().get("recommendation_score");
                return score instanceof Number ? ((Number) score).doubleValue() : 0.0;
            }
        } catch (Exception e) {
            log.error("Erreur prediction Flask: {}", e.getMessage());
        }
        return 0.0;
    }



    private Map<String, Object> buildFeatures(Long userId, Product product,
                                               List<Long> favoriteProductIds,
                                               List<Long> favoriteCategoryIds) {
        Long categoryId = product.getCategory() != null ? product.getCategory().getId() : 0L;

        boolean isFavorite = favoriteProductIds.contains(product.getId());
        boolean isPreferredCategory = favoriteCategoryIds.contains(categoryId);

        long searchFreq = favoriteRepository.countFavoritesByUserAndProductCategory(userId, categoryId);

        Map<String, Object> features = new HashMap<>();
        features.put("product_id", product.getId());
        features.put("category_id", categoryId);
        features.put("category_name_encoded", (int)(categoryId - 1));
        features.put("product_type_encoded", getProductTypeEncoded(product));
        features.put("searched_size_encoded", 5);
        features.put("size_available", true);
        features.put("is_preferred_category", isPreferredCategory);   
        features.put("search_frequency_in_category", (int) searchFreq);
        features.put("is_in_favorites", isFavorite);            
        features.put("prix", product.getPrix() != null ? product.getPrix().doubleValue() : 50.0);
        features.put("stock", product.getStock() != null ? product.getStock() : 0);
        features.put("product_status_encoded", getStatusEncoded(product));

        log.debug("Product {}: fav={}, prefCat={}, freq={}", product.getId(), isFavorite, isPreferredCategory, searchFreq);
        return features;
    }


    @Transactional(readOnly = true)
    private List<Map<String, Object>> fallbackRecommendations(Long userId, List<Product> products, int limit) {
        List<Long> favoriteProductIds = favoriteRepository.findProductIdsByUserId(userId);
        List<Long> favoriteCategoryIds = favoriteRepository.findFavoriteCategoryIdsByUserId(userId);

        List<Map<String, Object>> results = new ArrayList<>();
        for (Product p : products) {
            Long catId = p.getCategory() != null ? p.getCategory().getId() : 0L;
            double score = 30.0; 
            if (favoriteProductIds.contains(p.getId()))  score += 40.0;  
            if (favoriteCategoryIds.contains(catId))      score += 25.0;
            if (p.getStock() != null && p.getStock() > 0) score += 5.0;  

            Map<String, Object> item = new HashMap<>();
            item.put("product_id", p.getId());
            item.put("recommendation_score", score);
            item.put("priority", score > 70 ? "HIGH" : score > 55 ? "MEDIUM" : "LOW");
            item.put("rank", 0);
            results.add(item);
        }

        results.sort((a, b) -> Double.compare(
                (Double) b.get("recommendation_score"),
                (Double) a.get("recommendation_score")));

        for (int i = 0; i < results.size(); i++) results.get(i).put("rank", i + 1);
        return results.stream().limit(limit).collect(Collectors.toList());
    }


    public Map<String, Object> getModelInfo() {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(
                    FLASK_API_URL + "/api/model-info", Map.class);
            return response.getBody();
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "API Flask indisponible: " + e.getMessage());
            return error;
        }
    }


    private int getProductTypeEncoded(Product product) {
        if (product.getNom() == null) return 0;
        String name = product.getNom().toLowerCase();
        if (name.contains("chaussure") || name.contains("crampon") || name.contains("basket")) return 1;
        if (name.contains("maillot") || name.contains("short") || name.contains("legging") || name.contains("veste")) return 3;
        if (name.contains("ballon") || name.contains("raquette") || name.contains("haltere") || name.contains("sac de frappe")) return 2;
        return 0;
    }

    private int getStatusEncoded(Product product) {
        if (product.getStatus() == null) return 0;
        switch (product.getStatus()) {
            case EN_STOCK:          return 0;
            case EN_ARRIVAGE:       return 1;
            case RUPTURE_DE_STOCK:  return 2;
            default:                return 0;
        }
    }
}
