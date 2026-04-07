package tn.esprit._4se2.pi.dto.Performance;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PerformanceResponse {
    private Long id;
    private Long playerId;
    private Long matchId;
    private int score;
    private int assists;
    private double distanceCovered;
    private int timePlayed;
    private double rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}