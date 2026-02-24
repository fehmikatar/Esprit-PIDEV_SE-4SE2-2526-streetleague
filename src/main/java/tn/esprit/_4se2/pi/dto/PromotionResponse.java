package tn.esprit._4se2.pi.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PromotionResponse {
    private Long id;
    private String name;
    private String promoCode;
    private double discount;
    private LocalDate startDate;
    private LocalDate endDate;
}