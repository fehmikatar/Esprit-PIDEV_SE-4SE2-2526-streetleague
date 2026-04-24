package tn.esprit._4se2.pi.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit._4se2.pi.dto.DietPlan.DietPlanRequest;
import tn.esprit._4se2.pi.dto.DietPlan.DietPlanResponse;
import tn.esprit._4se2.pi.entities.DietPlan;
import tn.esprit._4se2.pi.entities.HealthProfile;
import tn.esprit._4se2.pi.mappers.DietPlanMapper;
import tn.esprit._4se2.pi.repositories.DietPlanRepository;
import tn.esprit._4se2.pi.repositories.HealthProfileRepository;
import tn.esprit._4se2.pi.services.DietPlan.DietPlanService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DietPlanServiceTest {

    @Mock private DietPlanRepository dietPlanRepository;
    @Mock private HealthProfileRepository healthProfileRepository;
    @Mock private DietPlanMapper dietPlanMapper;
    @InjectMocks private DietPlanService dietPlanService;

    @Test
    void createDietPlan_Success() {
        Long profileId = 1L;
        HealthProfile profile = new HealthProfile();
        profile.setId(profileId);

        DietPlanRequest request = new DietPlanRequest();
        request.setHealthProfileId(profileId);
        request.setPlanName("Weight Loss");

        DietPlan plan = new DietPlan();
        plan.setId(100L);

        when(healthProfileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(dietPlanMapper.toEntity(request)).thenReturn(plan);
        when(dietPlanRepository.save(any(DietPlan.class))).thenReturn(plan);
        when(dietPlanMapper.toResponse(plan)).thenReturn(new DietPlanResponse());

        var response = dietPlanService.createDietPlan(request);
        assertThat(response).isNotNull();
    }

    @Test
    void activateDietPlan_Success() {
        Long planId = 1L;
        DietPlan plan = new DietPlan();
        plan.setId(planId);
        plan.setIsActive(false);

        when(dietPlanRepository.findById(planId)).thenReturn(Optional.of(plan));
        when(dietPlanRepository.save(any(DietPlan.class))).thenReturn(plan);

        dietPlanService.activateDietPlan(planId);
        assertThat(plan.getIsActive()).isTrue();
        verify(dietPlanRepository).save(plan);
    }

    @Test
    void getActiveDietPlansByHealthProfile_ReturnsOnlyActive() {
        Long profileId = 100L;
        DietPlan activePlan = new DietPlan();
        activePlan.setIsActive(true);
        when(dietPlanRepository.findByHealthProfileIdAndIsActiveTrue(profileId)).thenReturn(java.util.List.of(activePlan));
        var result = dietPlanService.getActiveDietPlansByHealthProfile(profileId);
        assertThat(result).hasSize(1);
    }
}