package tn.esprit._4se2.pi.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class FieldOwnerResponse {
    Long id;
    String firstName;
    String lastName;
    String email;
    String phone;
    String businessName;
    String businessLicense;
    String location;
    List<Long> sportSpaceIds;
    Double totalRevenue;
    Integer totalSportSpaces;
    LocalDateTime createdAt;
    Boolean isActive;
}