package tn.esprit._4se2.pi.dto.Promotion;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

@Data
@Builder
public class PromotionResponse {
    private Long id;
    private String name;
    private String promoCode;
    private double discount;
    private LocalDate startDate;
    private LocalDate endDate;
}