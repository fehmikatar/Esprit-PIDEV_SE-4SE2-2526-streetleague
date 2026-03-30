package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a doctor user (treated_by reference in MedicalRecord).
 * Extends User via JOINED inheritance.
 */
@Entity
@Table(name = "doctors")
@Getter
@Setter
@NoArgsConstructor
public class Doctor extends User {

    private String specialty;
    private String licenseNumber;
    private String hospital;

    public Doctor(String specialty, String licenseNumber, String hospital) {
        this.specialty = specialty;
        this.licenseNumber = licenseNumber;
        this.hospital = hospital;
    }
}
