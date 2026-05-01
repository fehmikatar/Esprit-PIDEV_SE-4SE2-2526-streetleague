package tn.esprit._4se2.pi.dto.HealthProfile;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ActivityRecommendationDto {
    String dayOfWeek;
    String activityName;
    Integer durationMinutes;
    String intensity; // Low, Moderate, High
    String description;
}
