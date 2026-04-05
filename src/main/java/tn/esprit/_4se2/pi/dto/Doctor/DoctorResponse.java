package tn.esprit._4se2.pi.dto.Doctor;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DoctorResponse {
    Long id;
    String firstName;
    String lastName;
    String specialty;
    String licenseNumber;
    String email;
    String phoneNumber;
    String address;
}
