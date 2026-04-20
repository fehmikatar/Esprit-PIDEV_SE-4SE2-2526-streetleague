package tn.esprit._4se2.pi.dto.PositionStats;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PositionStatsDTO {
    private String position;
    private Long playerCount;
    private Double avgLevel;
    private Double avgTotalXp;
    private Double avgXpPerGame;
    private Double expertPercentage;
}