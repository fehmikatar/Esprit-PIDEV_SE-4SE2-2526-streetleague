package tn.esprit._4se2.pi.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SportSpaceAvailabilityResponse {
    Long id;
    Long sportSpaceId;
    String sportSpaceName;
    LocalDateTime availableFrom;
    LocalDateTime availableTo;
    Integer totalSlots;
    Integer bookedSlots;
    Integer availableSlots;
    String status;
}