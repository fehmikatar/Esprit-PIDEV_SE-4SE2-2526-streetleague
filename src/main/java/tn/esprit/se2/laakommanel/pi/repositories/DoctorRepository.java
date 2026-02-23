package tn.esprit.se2.laakommanel.pi.repositories;


import tn.esprit.se2.laakommanel.pi.entites.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {

    // Trouver par spécialité
    List<Doctor> findBySpecialty(String specialty);

    // Trouver par nom
    List<Doctor> findByLastName(String lastName);

    // Recherche par nom ou prénom
    List<Doctor> findByFirstNameContainingOrLastNameContaining(String firstName, String lastName);

    // Trouver par numéro de licence
    Optional<Doctor> findByLicenseNumber(String licenseNumber);

    // Trouver par email
    Optional<Doctor> findByEmail(String email);

    // Compter par spécialité
    Long countBySpecialty(String specialty);

    // Liste des spécialités disponibles
    @Query("SELECT DISTINCT d.specialty FROM Doctor d ORDER BY d.specialty")
    List<String> findAllSpecialties();

    // Recherche avancée
    @Query("SELECT d FROM Doctor d WHERE " +
            "(:specialty IS NULL OR d.specialty = :specialty) AND " +
            "(:keyword IS NULL OR d.firstName LIKE %:keyword% OR d.lastName LIKE %:keyword%)")
    List<Doctor> searchDoctors(@Param("specialty") String specialty,
                               @Param("keyword") String keyword);
}