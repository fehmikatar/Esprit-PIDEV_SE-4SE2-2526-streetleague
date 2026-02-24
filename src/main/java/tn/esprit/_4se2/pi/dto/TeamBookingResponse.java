package tn.esprit._4se2.pi.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TeamBookingResponse {
    Long id;
    Long teamId;
    String teamName;
    Long sportSpaceId;
    String sportSpaceName;
    LocalDateTime bookingDate;
    LocalDateTime startTime;
    LocalDateTime endTime;
    Integer playerCount;
    BigDecimal totalPrice;
    String status;
    LocalDateTime createdAt;
}