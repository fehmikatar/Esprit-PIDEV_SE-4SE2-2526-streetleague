package tn.esprit._4se2.pi.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceRequest {

    @NotNull(message = "L'ID du joueur est obligatoire")
    private Long playerId;

    @NotNull(message = "L'ID du match est obligatoire")
    private Long matchId;

    @Min(value = 0, message = "Les buts doivent être ≥ 0")
    @Max(value = 20, message = "Les buts ne peuvent pas dépasser 20")
    private int score;

    @Min(value = 0, message = "Les passes décisives doivent être ≥ 0")
    @Max(value = 15, message = "Les passes décisives ne peuvent pas dépasser 15")
    private int assists;

    @DecimalMin(value = "0.0", message = "Distance parcourue ≥ 0")
    @DecimalMax(value = "50.0", message = "Distance parcourue ≤ 50 km")
    private double distanceCovered;

    @Min(value = 0, message = "Temps de jeu ≥ 0")
    @Max(value = 120, message = "Temps de jeu ≤ 120 minutes")
    private int timePlayed;

    @DecimalMin(value = "0.0", message = "Note ≥ 0")
    @DecimalMax(value = "10.0", message = "Note ≤ 10")
    private double rating;
}