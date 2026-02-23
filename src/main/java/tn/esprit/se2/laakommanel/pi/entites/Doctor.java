package tn.esprit.se2.laakommanel.pi.entites;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Table(name = "doctors")
@Data
public class Doctor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String specialty;
    private String licenseNumber;
    private String email;
    private String phoneNumber;
    private String address;

    @OneToMany(mappedBy = "doctor")
    private List<Appointment> appointments;
}