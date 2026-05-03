package tn.esprit._4se2.pi.services.BadgePlayer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.dto.Sponsor.PromoCodeDTO;
import tn.esprit._4se2.pi.dto.Promotion.PromotionRequest;
import tn.esprit._4se2.pi.entities.*;
import tn.esprit._4se2.pi.repositories.BadgePlayerRepository;
import tn.esprit._4se2.pi.services.PromoCode.IPromoCodeService;
import tn.esprit._4se2.pi.services.Promotion.IPromotionService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class BadgePlayerService implements IBadgePlayerService {

    private final BadgePlayerRepository badgePlayerRepository;
    private final IPromoCodeService promoCodeService;
    private final IPromotionService promotionService;

    private static final int THRESHOLD_LEVEL = 5;
    private static final int THRESHOLD_XP = 500;

    @Override
    @Transactional
    public BadgePlayer awardBadgeToPlayer(Player player, Badge badge, Performance performance) {
        // Vérification anti-doublon
        if (badgePlayerRepository.existsByPlayerAndBadge(player, badge)) {
            log.info("Le badge '{}' a déjà été attribué au joueur {}", badge.getName(), player.getId());
            return null;
        }

        BadgePlayer badgePlayer = new BadgePlayer();
        badgePlayer.setPlayer(player);
        badgePlayer.setBadge(badge);
        badgePlayer.setObtainDate(LocalDate.now());
        badgePlayer.setPerformance(performance);

        return badgePlayerRepository.save(badgePlayer);
    }

    private String generatePromoCode(Badge badge, Player player) {
        return String.format("BADGE_%d_%d_%d", badge.getId(), player.getId(), System.currentTimeMillis());
    }

    private BigDecimal calculateDiscountPercentage(int requiredXp) {
        double discount = requiredXp / 10.0;
        if (discount > 70.0) discount = 70.0;
        return BigDecimal.valueOf(discount);
    }
}