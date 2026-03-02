package tn.esprit._4se2.pi.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SportSpaceRequest {

    @NotNull(message = "Field Owner ID is required")
    Long fieldOwnerId;

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100)
    String name;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 500)
    String description;

    @NotBlank(message = "Address is required")
    String address;

    @NotBlank(message = "Location is required")
    String location;

    @NotBlank(message = "Sport type is required")
    String sportType;

    @NotNull(message = "Capacity is required")
    @Min(1)
    Integer capacity;

    @NotNull(message = "Hourly rate is required")
    @DecimalMin("0.0")
    BigDecimal hourlyRate;

    String amenities;

    Double latitude;

    Double longitude;

    @NotNull
    Boolean isAvailable;
}