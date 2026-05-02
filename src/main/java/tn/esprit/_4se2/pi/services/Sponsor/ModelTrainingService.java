package tn.esprit._4se2.pi.services.Sponsor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit._4se2.pi.entities.SponsoredClick;
import tn.esprit._4se2.pi.repositories.SponsoredClickRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class ModelTrainingService {

    private final SponsoredClickRepository clickRepository;

    private final Map<Long, Map<Long, Double>> userProductMatrix = new ConcurrentHashMap<>();
    private final Map<Long, double[]> userFactors = new ConcurrentHashMap<>();
    private final Map<Long, double[]> productFactors = new ConcurrentHashMap<>();

    private static final int NUM_FACTORS = 20;
    private static final double LEARNING_RATE = 0.01;
    private static final double REGULARIZATION = 0.02;
    private static final int NUM_EPOCHS = 50;

    public void trainModel() {
        log.info("🚀 Démarrage de l'entraînement SVD...");

        List<SponsoredClick> interactions = clickRepository.findAll();
        if (interactions.isEmpty()) {
            log.warn("⚠️ Aucune interaction trouvée pour l'entraînement");
            return;
        }

        buildUserProductMatrix(interactions);
        initializeFactors();
        trainSVD(interactions);

        log.info("✅ Entraînement SVD terminé !");
    }

    private void buildUserProductMatrix(List<SponsoredClick> interactions) {
        userProductMatrix.clear();
        for (SponsoredClick click : interactions) {
            Long userId = click.getUser() != null ? click.getUser().getId() : 0L;
            Long productId = click.getProduct().getId();
            double score = calculateInteractionScore(click);

            userProductMatrix.computeIfAbsent(userId, k -> new ConcurrentHashMap<>())
                    .put(productId, score);
        }
    }

    private double calculateInteractionScore(SponsoredClick click) {
        double score = 0.0;
        if (click.getIsPurchased() != null && click.getIsPurchased()) score += 3.0;
        if (click.getIsClicked() != null && click.getIsClicked()) score += 1.0;
        if (click.getRelevanceScore() != null) score += click.getRelevanceScore() / 100.0;
        return score;
    }

    private void initializeFactors() {
        Random random = new Random(42);

        for (Long userId : userProductMatrix.keySet()) {
            double[] factors = new double[NUM_FACTORS];
            for (int i = 0; i < NUM_FACTORS; i++) {
                factors[i] = random.nextDouble() * 0.1;
            }
            userFactors.put(userId, factors);
        }

        Set<Long> allProductIds = new HashSet<>();
        for (Map<Long, Double> productMap : userProductMatrix.values()) {
            allProductIds.addAll(productMap.keySet());
        }

        for (Long productId : allProductIds) {
            double[] factors = new double[NUM_FACTORS];
            for (int i = 0; i < NUM_FACTORS; i++) {
                factors[i] = random.nextDouble() * 0.1;
            }
            productFactors.put(productId, factors);
        }
    }

    private void trainSVD(List<SponsoredClick> interactions) {
        for (int epoch = 0; epoch < NUM_EPOCHS; epoch++) {
            double totalError = 0.0;

            for (SponsoredClick click : interactions) {
                Long userId = click.getUser() != null ? click.getUser().getId() : 0L;
                Long productId = click.getProduct().getId();
                double actualRating = calculateInteractionScore(click);

                double[] userVec = userFactors.get(userId);
                double[] productVec = productFactors.get(productId);

                if (userVec == null || productVec == null) continue;

                double prediction = 0.0;
                for (int i = 0; i < NUM_FACTORS; i++) {
                    prediction += userVec[i] * productVec[i];
                }

                double error = actualRating - prediction;
                totalError += Math.abs(error);

                for (int i = 0; i < NUM_FACTORS; i++) {
                    double userGrad = error * productVec[i] - REGULARIZATION * userVec[i];
                    double productGrad = error * userVec[i] - REGULARIZATION * productVec[i];

                    userVec[i] += LEARNING_RATE * userGrad;
                    productVec[i] += LEARNING_RATE * productGrad;
                }
            }

            if (epoch % 10 == 0) {
                log.debug("Epoch {}: Error = {}", epoch, totalError / interactions.size());
            }
        }
    }

    public double predictScore(Long userId, Long productId) {
        double[] userVec = userFactors.getOrDefault(userId, new double[NUM_FACTORS]);
        double[] productVec = productFactors.getOrDefault(productId, new double[NUM_FACTORS]);

        double score = 0.0;
        for (int i = 0; i < NUM_FACTORS; i++) {
            score += userVec[i] * productVec[i];
        }

        return Math.min(Math.max(score * 20, 0), 100);
    }

    public Map<String, Object> getModelStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("numUsers", userFactors.size());
        stats.put("numProducts", productFactors.size());
        stats.put("numFactors", NUM_FACTORS);
        stats.put("isTrained", !userFactors.isEmpty());
        return stats;
    }
}