// LoyaltyTierResponse.java
package tn.esprit._4se2.pi.dto.Loyalty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoyaltyTierResponse {
    private Long id;
    private Long programId;
    private String programName;
    private String name;
    private Integer level;
    private Integer pointsRequired;
    private Double extraDiscountRate;
    private String benefits;
    private String exclusiveAccessTag;
}