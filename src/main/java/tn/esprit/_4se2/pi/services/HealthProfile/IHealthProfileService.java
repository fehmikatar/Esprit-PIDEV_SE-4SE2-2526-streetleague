package tn.esprit._4se2.pi.services.HealthProfile;

import tn.esprit._4se2.pi.dto.HealthProfile.HealthProfileRequest;
import tn.esprit._4se2.pi.dto.HealthProfile.HealthProfileResponse;
import tn.esprit._4se2.pi.dto.HealthProfile.ActivityRecommendationDto;
import java.util.List;

public interface IHealthProfileService {
    HealthProfileResponse createHealthProfile(HealthProfileRequest request);
    HealthProfileResponse getHealthProfileById(Long id);
    HealthProfileResponse getHealthProfileByUserId(Long userId);
    HealthProfileResponse updateHealthProfile(Long id, HealthProfileRequest request);
    void deleteHealthProfile(Long id);
    List<HealthProfileResponse> getAllHealthProfiles();
    List<ActivityRecommendationDto> generateActivityPlanByUserId(Long userId);
    List<ActivityRecommendationDto> generateActivityPlanByUserIdAndWeek(Long userId, int weekNumber);


    List<ActivityRecommendationDto> generateActivityPlanByUserIdAndWeek(Long userId, int weekNumber, boolean ignoreInjuries);
    void sendDailyReport(Long userId);
    void sendWeeklySummary(Long userId);

    // AI Health Score Methods
    double predictScore(tn.esprit._4se2.pi.dto.Athlet.AthleteRequest req);
    String generateAssessment(tn.esprit._4se2.pi.dto.Athlet.AthleteRequest req, double score);
    String predictFuture(tn.esprit._4se2.pi.dto.Athlet.AthleteRequest req, double score);
    void saveScore(Long userId, Double score, String assessment, String prediction);
    List<tn.esprit._4se2.pi.entities.HealthMetrics> getHistory(Long userId);
    tn.esprit._4se2.pi.dto.Athlet.ChatResponse traiteChatMessage(String message);
}