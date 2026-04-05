package tn.esprit._4se2.pi.dto.HealthMetrics;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HealthMetricsResponse {
    Long id;
    Long healthProfileId;
    Double weight;
    Double muscleMass;
    Double bodyFat;
    Double hydration;
    Integer restingHeartRate;
    Integer systolicBP;
    Integer diastolicBP;
    Integer sleepHours;
    Integer stressLevel;
    Integer energyLevel;
    LocalDateTime measuredAt;
    String notes;
}