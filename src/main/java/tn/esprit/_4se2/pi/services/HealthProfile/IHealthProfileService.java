package tn.esprit._4se2.pi.services.HealthProfile;

import tn.esprit._4se2.pi.entities.HealthProfile;
import tn.esprit._4se2.pi.entities.FitnessStatus;
import java.util.List;

public interface IHealthProfileService {
    List<HealthProfile> getAllHealthProfiles();
    HealthProfile getHealthProfileById(Long id);
    HealthProfile createHealthProfile(HealthProfile healthProfile);
    HealthProfile updateHealthProfile(Long id, HealthProfile healthProfile);
    void deleteHealthProfile(Long id);
    HealthProfile getHealthProfileByUser(Long userId);
    List<HealthProfile> getHealthProfilesByFitnessStatus(FitnessStatus status);
}