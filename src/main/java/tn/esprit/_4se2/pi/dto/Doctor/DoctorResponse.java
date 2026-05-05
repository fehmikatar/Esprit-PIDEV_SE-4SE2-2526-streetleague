package tn.esprit._4se2.pi.dto.Doctor;

import lombok.*;
import lombok.experimental.FieldDefaults;
import com.fasterxml.jackson.annotation.JsonProperty;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DoctorResponse {

    // Identifiant unique du médecin
    Long id;

    // Informations de base sur le médecin
    String firstName;
    String lastName;
    String specialty;
    String licenseNumber;
    String email;
    String phoneNumber;
    String address;

    // Horaires de travail
    String workingHoursStart;
    String workingHoursEnd;

    // Champ boolean pour la disponibilité, mappé en JSON avec un nom personnalisé
    @JsonProperty("isAvailable")
    boolean available;
}
