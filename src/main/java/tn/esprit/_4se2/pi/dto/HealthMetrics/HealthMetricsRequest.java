package tn.esprit._4se2.pi.dto.HealthMetrics;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthMetricsRequest {
    private Long healthProfileId;
    private Integer systolicBP;
    private Integer diastolicBP;
    private Integer heartRate;
    private Double temperature;
    private Double weight;
    private Integer sleepHours;
    private Integer stepsCount;
    private String notes;
}