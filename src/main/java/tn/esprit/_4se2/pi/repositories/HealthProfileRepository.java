package tn.esprit._4se2.pi.repositories;

import tn.esprit._4se2.pi.entities.HealthProfile;
import tn.esprit._4se2.pi.entities.FitnessStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HealthProfileRepository extends JpaRepository<HealthProfile, Long> {

    // ✅ Corrigé: utiliser @Query pour user.id
    @Query("SELECT hp FROM HealthProfile hp WHERE hp.user.id = :userId")
    HealthProfile findByUserId(@Param("userId") Long userId);

    // ✅ OK: fitnessStatus est une propriété directe
    List<HealthProfile> findByFitnessStatus(FitnessStatus status);

    // ✅ OK: age est une propriété directe
    List<HealthProfile> findByAgeBetween(int minAge, int maxAge);

    // ✅ OK: déjà avec @Query
    @Query("SELECT hp FROM HealthProfile hp WHERE (hp.weight / (hp.height * hp.height)) BETWEEN :minBmi AND :maxBmi")
    List<HealthProfile> findByBmiBetween(@Param("minBmi") double minBmi, @Param("maxBmi") double maxBmi);

    // ✅ OK: déjà avec @Query
    @Query("SELECT hp FROM HealthProfile hp WHERE (hp.weight / (hp.height * hp.height)) > 25")
    List<HealthProfile> findOverweightProfiles();

    // ✅ OK: déjà avec @Query
    @Query("SELECT hp FROM HealthProfile hp WHERE hp.user.id = :userId AND hp.fitnessStatus = :status")
    HealthProfile findByUserIdAndFitnessStatus(@Param("userId") Long userId, @Param("status") FitnessStatus status);

    // ✅ OK: count by fitnessStatus
    Long countByFitnessStatus(FitnessStatus status);
}