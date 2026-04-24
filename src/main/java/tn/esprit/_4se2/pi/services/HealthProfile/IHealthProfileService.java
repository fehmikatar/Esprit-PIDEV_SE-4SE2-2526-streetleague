package tn.esprit._4se2.pi.services.HealthProfile;

import tn.esprit._4se2.pi.dto.HealthProfile.HealthProfileRequest;
import tn.esprit._4se2.pi.dto.HealthProfile.HealthProfileResponse;

import java.util.List;

public interface IHealthProfileService {
    HealthProfileResponse createHealthProfile(HealthProfileRequest request);
    HealthProfileResponse getHealthProfileById(Long id);
    HealthProfileResponse getHealthProfileByUserId(Long userId);
    HealthProfileResponse updateHealthProfile(Long id, HealthProfileRequest request);
    void deleteHealthProfile(Long id);

    List<HealthProfileResponse> getAllHealthProfiles();
}