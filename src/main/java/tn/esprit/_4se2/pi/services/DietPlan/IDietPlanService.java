package tn.esprit._4se2.pi.services.DietPlan;

import tn.esprit._4se2.pi.dto.DietPlan.DietPlanRequest;
import tn.esprit._4se2.pi.dto.DietPlan.DietPlanResponse;
import java.util.List;

public interface IDietPlanService {
    DietPlanResponse createDietPlan(DietPlanRequest request);
    DietPlanResponse getDietPlanById(Long id);
    List<DietPlanResponse> getAllDietPlans();
    List<DietPlanResponse> getDietPlansByHealthProfileId(Long healthProfileId);
    List<DietPlanResponse> getActiveDietPlans();
    List<DietPlanResponse> getActiveDietPlansByHealthProfile(Long healthProfileId);
    DietPlanResponse updateDietPlan(Long id, DietPlanRequest request);
    void deleteDietPlan(Long id);
    void activateDietPlan(Long id);
    void deactivateDietPlan(Long id);
}