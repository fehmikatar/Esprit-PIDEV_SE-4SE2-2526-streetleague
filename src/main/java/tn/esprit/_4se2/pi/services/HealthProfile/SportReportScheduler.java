package tn.esprit._4se2.pi.services.HealthProfile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.entities.HealthProfile;
import tn.esprit._4se2.pi.repositories.HealthProfileRepository;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SportReportScheduler {

    private final HealthProfileRepository healthProfileRepository;
    private final IHealthProfileService healthProfileService;

    /**
     * Envoi du bilan quotidien chaque matin à 8h00.
     */
    @Scheduled(cron = "0 0 8 * * *")
    public void scheduleDailyReports() {
        log.info("Starting scheduled daily sports reports");
        List<HealthProfile> profiles = healthProfileRepository.findAll();
        for (HealthProfile profile : profiles) {
            try {
                healthProfileService.sendDailyReport(profile.getUser().getId());
            } catch (Exception e) {
                log.error("Failed to send daily report for user {}: {}", profile.getUser().getId(), e.getMessage());
            }
        }
    }

    /**
     * Envoi du bilan hebdomadaire chaque dimanche soir à 20h00.
     */
    @Scheduled(cron = "0 0 20 * * SUN")
    public void scheduleWeeklySummaries() {
        log.info("Starting scheduled weekly sports summaries");
        List<HealthProfile> profiles = healthProfileRepository.findAll();
        for (HealthProfile profile : profiles) {
            try {
                healthProfileService.sendWeeklySummary(profile.getUser().getId());
            } catch (Exception e) {
                log.error("Failed to send weekly summary for user {}: {}", profile.getUser().getId(), e.getMessage());
            }
        }
    }
}
