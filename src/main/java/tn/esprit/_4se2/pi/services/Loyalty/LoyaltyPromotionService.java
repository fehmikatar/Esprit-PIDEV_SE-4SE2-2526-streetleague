package tn.esprit._4se2.pi.services.Loyalty;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit._4se2.pi.dto.Promotion.PromotionResponse;
import tn.esprit._4se2.pi.entities.Promotion;
import tn.esprit._4se2.pi.mappers.PromotionMapper;
import tn.esprit._4se2.pi.repositories.LoyaltyClientRepository;
import tn.esprit._4se2.pi.repositories.PromotionRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoyaltyPromotionService {

    private final PromotionRepository promotionRepository;
    private final PromotionMapper promotionMapper;
    private final LoyaltyClientRepository clientRepository;

    public List<PromotionResponse> getPromotionsForUser(Long userId) {
        var clientOpt = clientRepository.findByUserId(userId);
        Long userTierId = clientOpt.map(c -> c.getCurrentTier().getId()).orElse(null);

        List<Promotion> allPromotions = promotionRepository.findAll();
        LocalDate today = LocalDate.now();

        return allPromotions.stream()
                .filter(p -> p.getStartDate().isBefore(today) && p.getEndDate().isAfter(today))
                .filter(p -> p.getLoyaltyTier() == null ||
                        (userTierId != null && p.getLoyaltyTier().getId().equals(userTierId)))
                .map(promotionMapper::toResponse)
                .collect(Collectors.toList());
    }
}