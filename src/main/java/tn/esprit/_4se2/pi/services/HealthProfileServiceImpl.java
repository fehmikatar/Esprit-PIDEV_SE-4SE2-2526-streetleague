package tn.esprit._4se2.pi.services;

import tn.esprit._4se2.pi.entities.HealthProfile;
import tn.esprit._4se2.pi.entities.FitnessStatus;
import tn.esprit._4se2.pi.repositories.HealthProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HealthProfileServiceImpl implements IHealthProfileService {

    private final HealthProfileRepository healthProfileRepository;

    @Override
    public List<HealthProfile> getAllHealthProfiles() {
        return healthProfileRepository.findAll();
    }

    @Override
    public HealthProfile getHealthProfileById(Long id) {
        return healthProfileRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("HealthProfile not found with id: " + id));
    }

    @Override
    public HealthProfile createHealthProfile(HealthProfile healthProfile) {
        return healthProfileRepository.save(healthProfile);
    }

    @Override
    public HealthProfile updateHealthProfile(Long id, HealthProfile profileDetails) {
        HealthProfile profile = getHealthProfileById(id);
        profile.setHeight(profileDetails.getHeight());
        profile.setWeight(profileDetails.getWeight());
        profile.setAge(profileDetails.getAge());
        profile.setFitnessStatus(profileDetails.getFitnessStatus());
        profile.setUser(profileDetails.getUser());
        return healthProfileRepository.save(profile);
    }

    @Override
    public void deleteHealthProfile(Long id) {
        HealthProfile profile = getHealthProfileById(id);
        healthProfileRepository.delete(profile);
    }

    @Override
    public HealthProfile getHealthProfileByUser(Long userId) {
        return healthProfileRepository.findByUserId(userId);
    }

    @Override
    public List<HealthProfile> getHealthProfilesByFitnessStatus(FitnessStatus status) {
        return healthProfileRepository.findByFitnessStatus(status);
    }
}