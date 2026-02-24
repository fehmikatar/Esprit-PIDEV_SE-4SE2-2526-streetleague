package tn.esprit._4se2.pi.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SportSpaceResponse {
    Long id;
    Long fieldOwnerId;
    String fieldOwnerName;
    String name;
    String description;
    String address;
    String location;
    String sportType;
    Integer capacity;
    BigDecimal hourlyRate;
    String amenities;
    Boolean isAvailable;
    Double averageRating;
    Integer totalBookings;
}