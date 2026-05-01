package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@Entity
@Table(name = "doctors")
@Data
@EqualsAndHashCode(callSuper = true)
public class Doctor extends User {
    private String specialty;
    private String licenseNumber;
    private String workingHoursStart;
    private String workingHoursEnd;
    
    @JsonProperty("isAvailable")
    private boolean available = true;

    @OneToMany(mappedBy = "doctor")
    private List<Appointment> appointments;
}