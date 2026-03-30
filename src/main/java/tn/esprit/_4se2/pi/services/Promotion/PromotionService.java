package tn.esprit._4se2.pi.services.Promotion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.dto.Promotion.PromotionRequest;
import tn.esprit._4se2.pi.dto.Promotion.PromotionResponse;
import tn.esprit._4se2.pi.entities.Promotion;
import tn.esprit._4se2.pi.exception.DuplicateResourceException;
import tn.esprit._4se2.pi.exception.ResourceNotFoundException;
import tn.esprit._4se2.pi.mappers.PromotionMapper;
import tn.esprit._4se2.pi.repositories.PromotionRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PromotionService implements IPromotionService {

    private final PromotionRepository promotionRepository;
    private final PromotionMapper promotionMapper;

    @Override
    public PromotionResponse createPromotion(PromotionRequest request) {
        log.info("Creating promotion with code: {}", request.getPromoCode());

        // Vérifier l'unicité du promoCode
        if (promotionRepository.findByPromoCode(request.getPromoCode()) != null) {
            throw new DuplicateResourceException("Un code promo avec cette valeur existe déjà");
        }

        Promotion promotion = promotionMapper.toEntity(request);
        Promotion saved = promotionRepository.save(promotion);
        log.info("Promotion created with id: {}", saved.getId());

        return promotionMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PromotionResponse getPromotionById(Long id) {
        log.info("Fetching promotion with id: {}", id);
        return promotionRepository.findById(id)
                .map(promotionMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion non trouvée avec l'id : " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PromotionResponse> getAllPromotions() {
        log.info("Fetching all promotions");
        return promotionRepository.findAll()
                .stream()
                .map(promotionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PromotionResponse updatePromotion(Long id, PromotionRequest request) {
        log.info("Updating promotion with id: {}", id);

        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion non trouvée avec l'id : " + id));

        // Vérifier l'unicité du promoCode si modifié
        Promotion existing = promotionRepository.findByPromoCode(request.getPromoCode());
        if (existing != null && !existing.getId().equals(id)) {
            throw new DuplicateResourceException("Un code promo avec cette valeur existe déjà");
        }

        promotionMapper.updateEntity(request, promotion);
        Promotion updated = promotionRepository.save(promotion);
        log.info("Promotion updated with id: {}", id);

        return promotionMapper.toResponse(updated);
    }

    @Override
    public void deletePromotion(Long id) {
        log.info("Deleting promotion with id: {}", id);
        if (!promotionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Promotion non trouvée avec l'id : " + id);
        }
        promotionRepository.deleteById(id);
        log.info("Promotion deleted with id: {}", id);
    }
}