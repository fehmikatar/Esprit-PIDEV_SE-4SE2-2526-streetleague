package tn.esprit._4se2.pi.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit._4se2.pi.dto.PromoCodeDTO;
import tn.esprit._4se2.pi.repository.PromoCodeRepository;
import tn.esprit._4se2.pi.entities.PromoCode;
import tn.esprit._4se2.pi.mappers.PromoCodeMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PromoCodeServiceImpl implements IPromoCodeService {

    private final PromoCodeRepository promoCodeRepository;
    private final PromoCodeMapper promoCodeMapper;


    @Override
    public PromoCodeDTO addPromoCode(PromoCodeDTO promoCodeDTO) {
        if (promoCodeRepository.existsByCode(promoCodeDTO.getCode())) {
            return null;
        }

        PromoCode promoCode = promoCodeMapper.toEntity(promoCodeDTO);
        promoCode.setCreatedAt(LocalDateTime.now());

        PromoCode saved = promoCodeRepository.save(promoCode);
        return promoCodeMapper.toDTO(saved);
    }

    @Override
    public List<PromoCodeDTO> addPromoCodes(List<PromoCodeDTO> promoCodeDTOs) {
        return promoCodeDTOs.stream()
                .map(this::addPromoCode)
                .collect(Collectors.toList());
    }

    @Override
    public PromoCodeDTO updatePromoCode(Long id, PromoCodeDTO promoCodeDTO) {
        Optional<PromoCode> optionalPromoCode = promoCodeRepository.findById(id);
        if (optionalPromoCode.isEmpty()) return null;

        PromoCode promoCode = optionalPromoCode.get();

        if (!promoCode.getCode().equals(promoCodeDTO.getCode()) &&
                promoCodeRepository.existsByCode(promoCodeDTO.getCode())) {
            return null;
        }

        promoCode.setCode(promoCodeDTO.getCode());
        promoCode.setDiscountType(PromoCode.DiscountType.valueOf(promoCodeDTO.getDiscountType()));
        promoCode.setDiscountValue(promoCodeDTO.getDiscountValue());
        promoCode.setExpiryDate(promoCodeDTO.getExpiryDate());
        promoCode.setUsageLimit(promoCodeDTO.getUsageLimit());
        promoCode.setActive(promoCodeDTO.getActive() != null ? promoCodeDTO.getActive() : true);

        PromoCode updated = promoCodeRepository.save(promoCode);
        return promoCodeMapper.toDTO(updated);
    }

    @Override
    public PromoCodeDTO getPromoCodeById(Long id) {
        return promoCodeRepository.findById(id)
                .map(promoCodeMapper::toDTO)
                .orElse(null);
    }

    @Override
    public PromoCodeDTO getPromoCodeByIdOrElse(Long id) {
        PromoCode defaultPromoCode = PromoCode.builder()
                .id(0L)
                .code("DEFAULT")
                .discountType(PromoCode.DiscountType.PERCENTAGE)
                .discountValue(BigDecimal.ZERO)
                .active(false)
                .createdAt(LocalDateTime.now())
                .build();

        PromoCode promoCode = promoCodeRepository.findById(id).orElse(defaultPromoCode);
        return promoCodeMapper.toDTO(promoCode);
    }

    @Override
    public PromoCodeDTO getPromoCodeByCode(String code) {
        return promoCodeRepository.findByCode(code)
                .map(promoCodeMapper::toDTO)
                .orElse(null);
    }

    @Override
    public List<PromoCodeDTO> getAllActivePromoCodes() {
        return promoCodeRepository.findByActiveTrue().stream()
                .map(promoCodeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PromoCodeDTO> getAllPromoCodes() {
        return promoCodeRepository.findAll().stream()
                .map(promoCodeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deletePromoCodeById(Long id) {
        promoCodeRepository.deleteById(id);
    }

    @Override
    public void deleteAllPromoCodes() {
        promoCodeRepository.deleteAll();
    }

    @Override
    public long countPromoCodes() {
        return promoCodeRepository.count();
    }

    @Override
    public boolean existsById(Long id) {
        return promoCodeRepository.existsById(id);
    }

    @Override
    public boolean existsByCode(String code) {
        return promoCodeRepository.existsByCode(code);
    }

    @Override
    public boolean isValidPromoCode(String code) {
        return promoCodeRepository.findByCode(code)
                .map(PromoCode::isValid)
                .orElse(false);
    }

    @Override
    public PromoCodeDTO applyPromoCode(String code, BigDecimal amount) {
        return promoCodeRepository.findByCode(code)
                .filter(PromoCode::isValid)
                .map(promoCode -> {
                    BigDecimal discountedAmount = promoCode.applyDiscount(amount);
                    PromoCodeDTO result = promoCodeMapper.toDTO(promoCode);
                    result.setDiscountValue(discountedAmount);
                    return result;
                })
                .orElse(null);
    }

    @Override
    public void deactivateExpiredPromoCodes() {
        List<PromoCode> expiredCodes = promoCodeRepository.findExpiredActivePromoCodes(LocalDateTime.now());
        expiredCodes.forEach(code -> code.setActive(false));
        promoCodeRepository.saveAll(expiredCodes);
    }
}
