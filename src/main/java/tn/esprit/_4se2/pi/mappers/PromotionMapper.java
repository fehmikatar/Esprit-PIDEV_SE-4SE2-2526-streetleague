package tn.esprit._4se2.pi.mappers;

import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.dto.Promotion.PromotionRequest;
import tn.esprit._4se2.pi.dto.Promotion.PromotionResponse;
import tn.esprit._4se2.pi.entities.Promotion;

@Component
public class PromotionMapper {

    public Promotion toEntity(PromotionRequest request) {
        Promotion promotion = new Promotion();
        promotion.setName(request.getName());
        promotion.setPromoCode(request.getPromoCode());
        promotion.setDiscount(request.getDiscount());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
        return promotion;
    }

    public PromotionResponse toResponse(Promotion promotion) {
        return PromotionResponse.builder()
                .id(promotion.getId())
                .name(promotion.getName())
                .promoCode(promotion.getPromoCode())
                .discount(promotion.getDiscount())
                .startDate(promotion.getStartDate())
                .endDate(promotion.getEndDate())
                .build();
    }

    public void updateEntity(PromotionRequest request, Promotion promotion) {
        promotion.setName(request.getName());
        promotion.setPromoCode(request.getPromoCode());
        promotion.setDiscount(request.getDiscount());
        promotion.setStartDate(request.getStartDate());
        promotion.setEndDate(request.getEndDate());
    }
}