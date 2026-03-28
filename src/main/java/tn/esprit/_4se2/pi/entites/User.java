package tn.esprit._4se2.pi.entites;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;

    @Column(unique = true)
    private String email;

    private String phone;
    private String passwordHash;
    private LocalDateTime createdAt;
    private boolean isActive;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    // Champs spécifiques aux docteurs
    private String specialty;
    private String licenseNumber;
    private String address;

    // Relations
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> appointments = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private HealthProfile healthProfile;

    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Appointment> doctorAppointments = new ArrayList<>();

    @OneToMany(mappedBy = "treatedBy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MedicalRecord> treatedRecords = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (role == null) {
            role = UserRole.PATIENT;
        }
    }

    // Méthodes utilitaires
    public boolean isDoctor() {
        return UserRole.DOCTOR.equals(role);
    }

    public boolean isPatient() {
        return UserRole.PATIENT.equals(role);
    }

    public boolean isAdmin() {
        return UserRole.ADMIN.equals(role);
    }



    public void removeAppointment(Appointment appointment) {
        appointments.remove(appointment);
        appointment.setUser(null);
    }



    public void removeDoctorAppointment(Appointment appointment) {
        doctorAppointments.remove(appointment);
        appointment.setDoctor(null);
    }

    public void addTreatedRecord(MedicalRecord record) {
        treatedRecords.add(record);
        record.setTreatedBy(this);
    }

    public void removeTreatedRecord(MedicalRecord record) {
        treatedRecords.remove(record);
        record.setTreatedBy(null);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", isActive=" + isActive +
                '}';
    }
}