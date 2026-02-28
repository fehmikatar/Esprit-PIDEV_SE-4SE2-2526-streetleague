package tn.esprit._4se2.pi.mappers;

import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.dto.PromoCodeDTO;
import tn.esprit._4se2.pi.entities.PromoCode;

@Component
public class PromoCodeMapper {

    public PromoCode toEntity(PromoCodeDTO dto) {
        if (dto == null) return null;

        return PromoCode.builder()
                .code(dto.getCode())
                .discountType(PromoCode.DiscountType.valueOf(dto.getDiscountType()))
                .discountValue(dto.getDiscountValue())
                .expiryDate(dto.getExpiryDate())
                .usageLimit(dto.getUsageLimit())
                .active(dto.getActive() != null ? dto.getActive() : true)
                .build();
    }

    public PromoCodeDTO toDTO(PromoCode entity) {
        if (entity == null) return null;

        return PromoCodeDTO.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .discountType(entity.getDiscountType().name())
                .discountValue(entity.getDiscountValue())
                .expiryDate(entity.getExpiryDate())
                .usageLimit(entity.getUsageLimit())
                .timesUsed(entity.getTimesUsed())
                .active(entity.getActive())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}
