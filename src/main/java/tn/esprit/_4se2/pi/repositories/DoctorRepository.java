package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.entities.Doctor;
import java.util.Optional;
import java.util.List;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByEmail(String email);

    default Optional<Doctor> findByLicenseNumber(String licenseNumber) {
        return findAll().stream()
                .filter(doctor -> doctor.getLicenseNumber() != null)
                .filter(doctor -> doctor.getLicenseNumber().equalsIgnoreCase(licenseNumber))
                .findFirst();
    }

    default List<Doctor> findBySpecialty(String specialty) {
        return findAll().stream()
                .filter(doctor -> doctor.getSpecialty() != null)
                .filter(doctor -> doctor.getSpecialty().equalsIgnoreCase(specialty))
                .toList();
    }

    default List<Doctor> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName) {
        String safeFirstName = firstName == null ? "" : firstName.toLowerCase();
        String safeLastName = lastName == null ? "" : lastName.toLowerCase();

        return findAll().stream()
                .filter(doctor -> {
                    String doctorFirstName = doctor.getFirstName() == null ? "" : doctor.getFirstName().toLowerCase();
                    String doctorLastName = doctor.getLastName() == null ? "" : doctor.getLastName().toLowerCase();

                    return doctorFirstName.contains(safeFirstName) || doctorLastName.contains(safeLastName);
                })
                .toList();
    }
}
