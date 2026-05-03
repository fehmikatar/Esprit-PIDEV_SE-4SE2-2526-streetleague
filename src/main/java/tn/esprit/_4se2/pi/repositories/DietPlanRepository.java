package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.entities.DietPlan;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface DietPlanRepository extends JpaRepository<DietPlan, Long> {
    List<DietPlan> findByHealthProfileId(Long healthProfileId);
    List<DietPlan> findByIsActiveTrue();
    List<DietPlan> findByHealthProfileIdAndIsActiveTrue(Long healthProfileId);
    List<DietPlan> findByStartDateAfter(LocalDate date);
    List<DietPlan> findByEndDateBefore(LocalDate date);
    List<DietPlan> findByEndDateBeforeAndIsActiveTrue(LocalDate date);
}