package tn.esprit._4se2.pi.services.DietPlan;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.dto.DietPlan.DietPlanRequest;
import tn.esprit._4se2.pi.dto.DietPlan.DietPlanResponse;
import tn.esprit._4se2.pi.entities.DietPlan;
import tn.esprit._4se2.pi.entities.HealthProfile;
import tn.esprit._4se2.pi.mappers.DietPlanMapper;
import tn.esprit._4se2.pi.repositories.DietPlanRepository;
import tn.esprit._4se2.pi.repositories.HealthProfileRepository;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DietPlanService implements IDietPlanService {

    private final DietPlanRepository dietPlanRepository;
    private final HealthProfileRepository healthProfileRepository;
    private final DietPlanMapper dietPlanMapper;

    @Override
    public DietPlanResponse createDietPlan(DietPlanRequest request) {
        log.info("Creating diet plan for health profile id: {}", request.getHealthProfileId());

        HealthProfile healthProfile = healthProfileRepository.findById(request.getHealthProfileId())
                .orElseThrow(() -> new RuntimeException("Health profile not found with id: " + request.getHealthProfileId()));

        DietPlan dietPlan = dietPlanMapper.toEntity(request);
        dietPlan.setHealthProfile(healthProfile);

        // Default isActive if not set
        if (dietPlan.getIsActive() == null) {
            dietPlan.setIsActive(true);
        }

        DietPlan saved = dietPlanRepository.save(dietPlan);
        log.info("Diet plan created with id: {}", saved.getId());

        return dietPlanMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public DietPlanResponse getDietPlanById(Long id) {
        log.info("Fetching diet plan with id: {}", id);
        return dietPlanRepository.findById(id)
                .map(dietPlanMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Diet plan not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DietPlanResponse> getAllDietPlans() {
        log.info("Fetching all diet plans");
        return dietPlanRepository.findAll()
                .stream()
                .map(dietPlanMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DietPlanResponse> getDietPlansByHealthProfileId(Long healthProfileId) {
        log.info("Fetching diet plans for health profile id: {}", healthProfileId);
        return dietPlanRepository.findByHealthProfileId(healthProfileId)
                .stream()
                .map(dietPlanMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DietPlanResponse> getActiveDietPlans() {
        log.info("Fetching active diet plans");
        return dietPlanRepository.findByIsActiveTrue()
                .stream()
                .map(dietPlanMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DietPlanResponse> getActiveDietPlansByHealthProfile(Long healthProfileId) {
        log.info("Fetching active diet plans for health profile id: {}", healthProfileId);
        return dietPlanRepository.findByHealthProfileIdAndIsActiveTrue(healthProfileId)
                .stream()
                .map(dietPlanMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DietPlanResponse updateDietPlan(Long id, DietPlanRequest request) {
        log.info("Updating diet plan with id: {}", id);

        DietPlan existing = dietPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Diet plan not found with id: " + id));

        // Check if health profile changed
        if (!existing.getHealthProfile().getId().equals(request.getHealthProfileId())) {
            HealthProfile newHealthProfile = healthProfileRepository.findById(request.getHealthProfileId())
                    .orElseThrow(() -> new RuntimeException("Health profile not found with id: " + request.getHealthProfileId()));
            existing.setHealthProfile(newHealthProfile);
        }

        dietPlanMapper.updateEntity(request, existing);
        DietPlan updated = dietPlanRepository.save(existing);
        log.info("Diet plan updated with id: {}", id);

        return dietPlanMapper.toResponse(updated);
    }

    @Override
    public void deleteDietPlan(Long id) {
        log.info("Deleting diet plan with id: {}", id);
        if (!dietPlanRepository.existsById(id)) {
            throw new RuntimeException("Diet plan not found with id: " + id);
        }
        dietPlanRepository.deleteById(id);
        log.info("Diet plan deleted with id: {}", id);
    }

    @Override
    public void activateDietPlan(Long id) {
        log.info("Activating diet plan with id: {}", id);
        DietPlan dietPlan = dietPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Diet plan not found with id: " + id));
        dietPlan.setIsActive(true);
        dietPlanRepository.save(dietPlan);
        log.info("Diet plan activated with id: {}", id);
    }

    @Override
    public void deactivateDietPlan(Long id) {
        log.info("Deactivating diet plan with id: {}", id);
        DietPlan dietPlan = dietPlanRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Diet plan not found with id: " + id));
        dietPlan.setIsActive(false);
        dietPlanRepository.save(dietPlan);
        log.info("Diet plan deactivated with id: {}", id);
    }
}