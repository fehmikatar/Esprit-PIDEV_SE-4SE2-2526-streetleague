package tn.esprit._4se2.pi.services.Sponsor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit._4se2.pi.entities.Product;
import tn.esprit._4se2.pi.entities.Sponsor;
import tn.esprit._4se2.pi.entities.SponsoredProduct;
import tn.esprit._4se2.pi.repositories.ProductRepository;
import tn.esprit._4se2.pi.repositories.SponsorRepository;
import tn.esprit._4se2.pi.repositories.SponsoredProductRepository;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BiddingService {

    private final SponsorRepository sponsorRepository;
    private final SponsoredProductRepository sponsoredProductRepository;
    private final ProductRepository productRepository;
    private final ModelTrainingService modelTrainingService;

    /**
     * Calcule le score d'enchère pour un produit sponsorisé
     */
    public double calculateBidScore(SponsoredProduct sponsored, Long userId, int position) {
        Product product = sponsored.getProduct();
        Sponsor sponsor = sponsored.getSponsor();

        // 1. Score de pertinence IA (0-100)
        double relevanceScore = modelTrainingService.predictScore(userId, product.getId());

        // 2. Score d'enchère (0-100)
        double bidScore = Math.min(sponsored.getBidAmount() / 10.0, 100.0);

        // 3. Score de budget restant (0-100)
        double budgetScore = (sponsor.getRemainingBudget() / sponsor.getDailyBudget()) * 100;

        // 4. Score de position (position 1 = meilleure)
        double positionScore = Math.max(0, 100 - (position * 10));

        // 5. Score de popularité du produit
        double popularityScore = Math.min(product.getStock() > 0 ? 50 : 0, 100);

        // Score final pondéré
        double finalScore = (relevanceScore * 0.35) +
                (bidScore * 0.30) +
                (budgetScore * 0.15) +
                (positionScore * 0.10) +
                (popularityScore * 0.10);

        return Math.min(finalScore, 100);
    }

    /**
     * Sélectionne les meilleures enchères pour l'affichage
     */
    public List<SponsoredProduct> selectWinningBids(Long userId, int limit) {
        List<SponsoredProduct> activeSponsored = sponsoredProductRepository.findByIsActiveTrue();

        // Vérifier les limites quotidiennes
        activeSponsored = activeSponsored.stream()
                .filter(sp -> sp.getTodayImpressions() < sp.getDailyImpressionLimit())
                .filter(sp -> sp.getTodayClicks() < sp.getDailyClickLimit())
                .filter(sp -> sp.getSponsor().getRemainingBudget() > 0)
                .collect(Collectors.toList());

        // Calculer les scores pour chaque position
        List<SponsoredProduct> winners = new ArrayList<>();

        for (int position = 0; position < limit && position < activeSponsored.size(); position++) {
            final int currentPosition = position;
            SponsoredProduct winner = activeSponsored.stream()
                    .max((a, b) -> Double.compare(
                            calculateBidScore(a, userId, currentPosition + 1),
                            calculateBidScore(b, userId, currentPosition + 1)
                    ))
                    .orElse(null);

            if (winner != null) {
                winners.add(winner);
                activeSponsored.remove(winner);
            }
        }

        return winners;
    }

    /**
     * Enregistre une impression et débite le sponsor
     */
    public void recordImpressionAndCharge(Long sponsoredProductId) {
        SponsoredProduct sponsored = sponsoredProductRepository.findById(sponsoredProductId).orElse(null);
        if (sponsored != null) {
            sponsored.setTodayImpressions(sponsored.getTodayImpressions() + 1);
            sponsoredProductRepository.save(sponsored);
        }
    }

    /**
     * Enregistre un clic et débite le sponsor
     */
    public void recordClickAndCharge(Long sponsoredProductId) {
        SponsoredProduct sponsored = sponsoredProductRepository.findById(sponsoredProductId).orElse(null);
        if (sponsored != null) {
            sponsored.setTodayClicks(sponsored.getTodayClicks() + 1);
            sponsoredProductRepository.save(sponsored);

            Sponsor sponsor = sponsored.getSponsor();
            sponsor.setRemainingBudget(sponsor.getRemainingBudget() - sponsored.getBidAmount());
            sponsor.setTotalClicks(sponsor.getTotalClicks() + 1);
            sponsor.setTotalSpent(sponsor.getTotalSpent() + sponsored.getBidAmount());
            sponsorRepository.save(sponsor);
        }
    }

    /**
     * Réinitialise les compteurs quotidiens
     */
    public void resetDailyCounters() {
        List<SponsoredProduct> allSponsored = sponsoredProductRepository.findAll();
        for (SponsoredProduct sp : allSponsored) {
            sp.setTodayImpressions(0);
            sp.setTodayClicks(0);
        }
        sponsoredProductRepository.saveAll(allSponsored);
        log.info("🔄 Compteurs quotidiens réinitialisés");
    }
}