package tn.esprit._4se2.pi.services.Sponsor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit._4se2.pi.dto.Sponsor.ProductDTOs;
import tn.esprit._4se2.pi.entities.Product;
import tn.esprit._4se2.pi.entities.SponsoredClick;
import tn.esprit._4se2.pi.mappers.ProductMapper;
import tn.esprit._4se2.pi.repositories.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SponsoredRecommendationService {

    private final ProductRepository productRepository;
    private final FavoriteRepository favoriteRepository;
    private final SponsoredClickRepository clickRepository;
    private final ProductMapper productMapper;
    private final ModelTrainingService modelTrainingService;

    public List<ProductDTOs.ProductResponse> getSponsoredRecommendations(Long userId, int limit) {
        Set<Long> seenProductIds = getSeenProductIds(userId);
        List<Product> recommendations = new ArrayList<>();

        List<Long> favoriteProductIds = favoriteRepository.findByUserId(userId)
                .stream()
                .map(fav -> fav.getProduct().getId())
                .collect(Collectors.toList());

        if (!favoriteProductIds.isEmpty()) {
            Set<Long> favoriteCategoryIds = favoriteRepository.findByUserId(userId)
                    .stream()
                    .map(fav -> fav.getProduct().getCategory().getId())
                    .collect(Collectors.toSet());

            for (Long categoryId : favoriteCategoryIds) {
                productRepository.findByCategoryIdAndDeletedFalse(categoryId)
                        .stream()
                        .filter(p -> !seenProductIds.contains(p.getId()))
                        .limit(5)
                        .forEach(recommendations::add);
            }
        }

        if (recommendations.size() < limit) {
            List<Product> allProducts = productRepository.findByDeletedFalse();
            allProducts.sort((p1, p2) -> {
                long f1 = favoriteRepository.countByProductId(p1.getId());
                long f2 = favoriteRepository.countByProductId(p2.getId());
                return Long.compare(f2, f1);
            });

            for (Product p : allProducts) {
                if (!seenProductIds.contains(p.getId()) && !recommendations.contains(p)) {
                    recommendations.add(p);
                    if (recommendations.size() >= limit) break;
                }
            }
        }

        return recommendations.stream()
                .limit(limit)
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<ProductDTOs.ProductResponse> getAdvancedRecommendations(Long userId, int limit) {
        List<Product> allProducts = productRepository.findByDeletedFalse();
        Set<Long> seenProductIds = getSeenProductIds(userId);

        List<Map.Entry<Long, Double>> scores = new ArrayList<>();

        for (Product product : allProducts) {
            if (!seenProductIds.contains(product.getId())) {
                double score = modelTrainingService.predictScore(userId, product.getId());
                scores.add(new AbstractMap.SimpleEntry<>(product.getId(), score));
            }
        }

        scores.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));

        List<Long> recommendedIds = scores.stream()
                .limit(limit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        return productRepository.findAllById(recommendedIds).stream()
                .map(productMapper::toDTO)
                .collect(Collectors.toList());
    }

    private Set<Long> getSeenProductIds(Long userId) {
        Set<Long> seen = new HashSet<>();

        favoriteRepository.findByUserId(userId)
                .forEach(fav -> seen.add(fav.getProduct().getId()));

        clickRepository.findByUserId(userId)
                .forEach(click -> seen.add(click.getProduct().getId()));

        return seen;
    }

    public double calculateRelevanceScore(Long userId, Product product) {
        if (product == null) return 0.0;

        double score = 0.0;

        long favoriteCount = favoriteRepository.countByProductId(product.getId());
        double favoriteScore = Math.min(favoriteCount / 100.0, 1.0);
        score += favoriteScore * 0.3;

        List<SponsoredClick> productClicks = clickRepository.findByProductId(product.getId());
        long impressions = productClicks.size();
        long clicks = productClicks.stream().filter(SponsoredClick::getIsClicked).count();
        double ctr = impressions > 0 ? (double) clicks / impressions : 0.05;
        score += Math.min(ctr, 0.5) * 0.4;

        double priceScore = Math.min(product.getPrix().doubleValue() / 200.0, 1.0);
        score += priceScore * 0.1;

        long daysSinceCreation = Duration.between(product.getCreatedAt(), LocalDateTime.now()).toDays();
        double newnessScore = Math.max(0, 1.0 - (daysSinceCreation / 30.0));
        score += newnessScore * 0.2;

        return Math.min(score, 1.0) * 100;
    }
}