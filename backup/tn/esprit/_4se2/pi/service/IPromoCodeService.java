package tn.esprit._4se2.pi.service;

import tn.esprit._4se2.pi.dto.PromoCodeDTO;

import java.math.BigDecimal;
import java.util.List;

public interface IPromoCodeService {
    PromoCodeDTO addPromoCode(PromoCodeDTO promoCodeDTO);
    List<PromoCodeDTO> addPromoCodes(List<PromoCodeDTO> promoCodeDTOs);

    PromoCodeDTO updatePromoCode(Long id, PromoCodeDTO promoCodeDTO);

    PromoCodeDTO getPromoCodeById(Long id);
    PromoCodeDTO getPromoCodeByIdOrElse(Long id);
    PromoCodeDTO getPromoCodeByCode(String code);
    List<PromoCodeDTO> getAllActivePromoCodes();
    List<PromoCodeDTO> getAllPromoCodes();

    void deletePromoCodeById(Long id);
    void deleteAllPromoCodes();

    long countPromoCodes();
    boolean existsById(Long id);
    boolean existsByCode(String code);
    boolean isValidPromoCode(String code);

    PromoCodeDTO applyPromoCode(String code, BigDecimal amount);
    void deactivateExpiredPromoCodes();
}
