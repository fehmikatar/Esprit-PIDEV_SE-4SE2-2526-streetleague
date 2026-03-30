package tn.esprit._4se2.pi.services;

import tn.esprit._4se2.pi.dto.PromotionRequest;
import tn.esprit._4se2.pi.dto.PromotionResponse;
import java.util.List;

public interface IPromotionService {
    PromotionResponse createPromotion(PromotionRequest request);
    PromotionResponse getPromotionById(Long id);
    List<PromotionResponse> getAllPromotions();
    PromotionResponse updatePromotion(Long id, PromotionRequest request);
    void deletePromotion(Long id);
}