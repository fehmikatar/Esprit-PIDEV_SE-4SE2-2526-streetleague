package tn.esprit._4se2.pi.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TeamBookingRequest {

    @NotNull(message = "Team ID is required")
    Long teamId;

    @NotNull(message = "Sport Space ID is required")
    Long sportSpaceId;

    @NotNull(message = "Start time is required")
    @FutureOrPresent
    LocalDateTime startTime;

    @NotNull(message = "End time is required")
    @FutureOrPresent
    LocalDateTime endTime;

    @NotNull(message = "Player count is required")
    @Min(1)
    Integer playerCount;
}