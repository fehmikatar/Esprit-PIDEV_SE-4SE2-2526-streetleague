package tn.esprit._4se2.pi.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit._4se2.pi.entities.Promotion;
import tn.esprit._4se2.pi.repositories.PromotionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PromotionService {

    private final PromotionRepository promotionRepository;

    // Create
    public Promotion createPromotion(Promotion promotion) {
        // Vérifier si le code promo est unique (optionnel)
        if (promotionRepository.findByPromoCode(promotion.getPromoCode()) != null) {
            throw new RuntimeException("Le code promo existe déjà");
        }
        return promotionRepository.save(promotion);
    }

    // Read all
    public List<Promotion> getAllPromotions() {
        return promotionRepository.findAll();
    }

    // Read by id
    public Promotion getPromotionById(Long id) {
        return promotionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Promotion non trouvée avec l'id : " + id));
    }

    // Update
    public Promotion updatePromotion(Long id, Promotion promotionDetails) {
        Promotion promotion = getPromotionById(id);
        promotion.setName(promotionDetails.getName());
        promotion.setPromoCode(promotionDetails.getPromoCode());
        promotion.setDiscount(promotionDetails.getDiscount());
        promotion.setStartDate(promotionDetails.getStartDate());
        promotion.setEndDate(promotionDetails.getEndDate());
        return promotionRepository.save(promotion);
    }

    // Delete
    public void deletePromotion(Long id) {
        Promotion promotion = getPromotionById(id);
        promotionRepository.delete(promotion);
    }
}