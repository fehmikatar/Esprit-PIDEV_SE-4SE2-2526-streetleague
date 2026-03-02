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
public class SportSpaceAvailabilityRequest {

    @NotNull(message = "Sport Space ID is required")
    Long sportSpaceId;

    @NotNull(message = "Available from is required")
    @FutureOrPresent
    LocalDateTime availableFrom;

    @NotNull(message = "Available to is required")
    @FutureOrPresent
    LocalDateTime availableTo;

    @NotNull(message = "Total slots is required")
    @Min(1)
    Integer totalSlots;
}