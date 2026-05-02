package tn.esprit._4se2.pi.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Doctor extends User {
    // TODO: choose the correct version where conflicts are unresolved.
    String specialty;
    String licenseNumber;
    String address;
    String workingHoursStart;
    String workingHoursEnd;

    @JsonProperty("isAvailable")
    boolean available = true;

    @OneToMany(mappedBy = "doctor")
    List<Appointment> appointments;
}
