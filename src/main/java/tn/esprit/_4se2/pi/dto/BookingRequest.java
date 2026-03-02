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
public class BookingRequest {

    @NotNull(message = "User ID is required")
    Long userId;  //yejbed objet mech variable id 3al koolll

    @NotNull(message = "Sport Space ID is required")
    Long sportSpaceId;

    @NotNull(message = "Start time is required")
    @FutureOrPresent(message = "Start time must be in the future")
    LocalDateTime startTime;

    @NotNull(message = "End time is required")
    @FutureOrPresent(message = "End time must be in the future")
    LocalDateTime endTime;
}