package tn.esprit._4se2.pi.services.Sponsor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.entities.Product;
import tn.esprit._4se2.pi.entities.SponsoredClick;
import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.repositories.ProductRepository;
import tn.esprit._4se2.pi.repositories.SponsoredClickRepository;
import tn.esprit._4se2.pi.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SponsoredTrackingService {

    private final SponsoredClickRepository clickRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public SponsoredClick recordImpression(Long userId, Long productId, Integer position,
                                           String sessionId, Double bidAmount, Double relevanceScore) {
        Optional<User> user = userId != null ? userRepository.findById(userId) : Optional.empty();
        Optional<Product> product = productRepository.findById(productId);

        if (product.isEmpty()) {
            log.warn("Produit non trouvé: {}", productId);
            return null;
        }

        SponsoredClick click = SponsoredClick.builder()
                .user(user.orElse(null))
                .product(product.get())
                .isClicked(false)
                .isPurchased(false)
                .sponsoredPosition(position)
                .sessionId(sessionId)
                .bidAmount(bidAmount)
                .relevanceScore(relevanceScore)
                .clickedAt(LocalDateTime.now())
                .build();

        return clickRepository.save(click);
    }

    @Transactional
    public SponsoredClick recordClick(Long clickId, Integer duration) {
        Optional<SponsoredClick> optional = clickRepository.findById(clickId);

        if (optional.isPresent()) {
            SponsoredClick click = optional.get();
            click.setIsClicked(true);
            click.setClickDuration(duration);
            log.info("💰 Clic sponsorisé enregistré - Product: {}", click.getProduct().getId());
            return clickRepository.save(click);
        }
        return null;
    }

    @Transactional
    public SponsoredClick recordPurchase(Long clickId) {
        Optional<SponsoredClick> optional = clickRepository.findById(clickId);

        if (optional.isPresent()) {
            SponsoredClick click = optional.get();
            click.setIsPurchased(true);
            log.info("🎉 Achat sponsorisé enregistré - Product: {}", click.getProduct().getId());
            return clickRepository.save(click);
        }
        return null;
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalImpressions", clickRepository.count());

        long totalClicks = 0;
        long totalPurchases = 0;
        for (SponsoredClick click : clickRepository.findAll()) {
            if (click.getIsClicked() != null && click.getIsClicked()) totalClicks++;
            if (click.getIsPurchased() != null && click.getIsPurchased()) totalPurchases++;
        }
        stats.put("totalClicks", totalClicks);
        stats.put("totalPurchases", totalPurchases);

        double globalCTR = clickRepository.count() > 0 ? (double) totalClicks / clickRepository.count() : 0;
        stats.put("globalCTR", globalCTR);

        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        long recentInteractions = 0;
        for (SponsoredClick click : clickRepository.findAll()) {
            if (click.getClickedAt() != null && click.getClickedAt().isAfter(twentyFourHoursAgo)) {
                recentInteractions++;
            }
        }
        stats.put("recentInteractions24h", recentInteractions);

        Map<Integer, Map<String, Long>> ctrByPosition = new HashMap<>();
        for (SponsoredClick click : clickRepository.findAll()) {
            if (click.getSponsoredPosition() != null) {
                Integer pos = click.getSponsoredPosition();
                ctrByPosition.putIfAbsent(pos, new HashMap<>());
                ctrByPosition.get(pos).putIfAbsent("impressions", 0L);
                ctrByPosition.get(pos).putIfAbsent("clicks", 0L);
                ctrByPosition.get(pos).put("impressions", ctrByPosition.get(pos).get("impressions") + 1);
                if (click.getIsClicked() != null && click.getIsClicked()) {
                    ctrByPosition.get(pos).put("clicks", ctrByPosition.get(pos).get("clicks") + 1);
                }
            }
        }
        stats.put("ctrByPosition", ctrByPosition);

        return stats;
    }
}