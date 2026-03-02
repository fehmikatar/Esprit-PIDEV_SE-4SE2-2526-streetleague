package tn.esprit._4se2.pi.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit._4se2.pi.dto.PromotionRequest;
import tn.esprit._4se2.pi.dto.PromotionResponse;
import tn.esprit._4se2.pi.entities.Promotion;
import tn.esprit._4se2.pi.repositories.PromotionRepository;
import tn.esprit._4se2.pi.exception.ResourceNotFoundException; // à créer
import tn.esprit._4se2.pi.exception.DuplicateResourceException; // à créer

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;

    public PromotionResponse createPromotion(PromotionRequest request) {
        // Vérifier l'unicité du promoCode
        if (promotionRepository.findByPromoCode(request.getPromoCode()) != null) {
            throw new DuplicateResourceException("Un code promo avec cette valeur existe déjà");
        }

        // Mapper Request → Entité
        Promotion promotion = new Promotion();
        promotion.setName(request.getName());
        promotion.setPromoCode(request.getPromoCode());
        promotion.setDiscount(request.getDiscount());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());

        Promotion saved = promotionRepository.save(promotion);
        return mapToResponse(saved);
    }

    public List<PromotionResponse> getAllPromotions() {
        return promotionRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public PromotionResponse getPromotionById(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion non trouvée avec l'id : " + id));
        return mapToResponse(promotion);
    }

    public PromotionResponse updatePromotion(Long id, PromotionRequest request) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion non trouvée avec l'id : " + id));

        // Vérifier unicité du promoCode si modifié
        Promotion existing = promotionRepository.findByPromoCode(request.getPromoCode());
        if (existing != null && !existing.getId().equals(id)) {
            throw new DuplicateResourceException("Un code promo avec cette valeur existe déjà");
        }

        // Mise à jour des champs
        promotion.setName(request.getName());
        promotion.setPromoCode(request.getPromoCode());
        promotion.setDiscount(request.getDiscount());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());

        Promotion updated = promotionRepository.save(promotion);
        return mapToResponse(updated);
    }

    public void deletePromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion non trouvée avec l'id : " + id));
        promotionRepository.delete(promotion);
    }

    // Méthode utilitaire pour mapper Entité → Response
    private PromotionResponse mapToResponse(Promotion promotion) {
        return PromotionResponse.builder()
                .id(promotion.getId())
                .name(promotion.getName())
                .promoCode(promotion.getPromoCode())
                .discount(promotion.getDiscount())
                .startDate(promotion.getStartDate())
                .endDate(promotion.getEndDate())
                .build();
    }
}