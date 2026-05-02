package tn.esprit._4se2.pi.services.Cart;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit._4se2.pi.repositories.CartRepository;
import tn.esprit._4se2.pi.repositories.ProductRepository;
import tn.esprit._4se2.pi.repositories.PromoCodeRepository;

import java.util.*;


@Service
@RequiredArgsConstructor
public class CartStatsService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final PromoCodeRepository promoCodeRepository;

    public List<Map<String, Object>> getTopSellingProducts() {
        List<Object[]> results = cartRepository.findTopSellingProductsWithCategory();
        List<Map<String, Object>> stats = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("productId", row[0]);
            map.put("productName", row[1]);
            map.put("categoryName", row[2]);
            map.put("quantitySold", row[3]);
            map.put("revenue", row[4]);
            stats.add(map);
        }
        return stats;
    }

    public List<Map<String, Object>> getAbandonedStatsByCity() {
        List<Object[]> results = cartRepository.findAbandonedCartStatsByCity();
        List<Map<String, Object>> stats = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("city", row[0]);
            map.put("count", row[1]);
            map.put("lostRevenue", row[2]);
            stats.add(map);
        }
        return stats;
    }

    public List<Map<String, Object>> getPromoCodeStats() {
        List<Object[]> results = promoCodeRepository.findPromoCodeRevenueStats();
        List<Map<String, Object>> stats = new ArrayList<>();

        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("code", row[0]);
            map.put("usageCount", row[1]);
            map.put("totalRevenue", row[2]);
            stats.add(map);
        }
        return stats;
    }
}
