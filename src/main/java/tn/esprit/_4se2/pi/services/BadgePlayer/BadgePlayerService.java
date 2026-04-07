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

        // Vérification des seuils pour générer un code promo
        if (badge.getLevel() >= THRESHOLD_LEVEL && badge.getRequiredXp() >= THRESHOLD_XP) {
            log.info("Badge '{}' atteint les seuils → génération d'un code promo", badge.getName());

            String uniqueCode = generatePromoCode(badge, player);
            BigDecimal discountPercent = calculateDiscountPercentage(badge.getRequiredXp());

            PromoCodeDTO promoCodeDTO = PromoCodeDTO.builder()
                    .code(uniqueCode)
                    .discountType("PERCENTAGE")
                    .discountValue(discountPercent)
                    .expiryDate(LocalDateTime.now().plusDays(30))
                    .usageLimit(1)
                    .active(true)
                    .build();

            PromoCodeDTO savedPromoCode = promoCodeService.addPromoCode(promoCodeDTO);
            if (savedPromoCode != null) {
                // Création de la promotion associée
                PromotionRequest promotionRequest = PromotionRequest.builder()
                        .name("Promotion badge: " + badge.getName())
                        .promoCode(uniqueCode)
                        .discount(discountPercent.doubleValue())
                        .startDate(LocalDate.now())
                        .endDate(LocalDate.now().plusDays(30))
                        .build();
                promotionService.createPromotion(promotionRequest);
                log.info("Code promo {} généré avec {}% de réduction", uniqueCode, discountPercent);
            } else {
                log.warn("Échec création code promo (peut-être duplicata)");
            }
        }

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