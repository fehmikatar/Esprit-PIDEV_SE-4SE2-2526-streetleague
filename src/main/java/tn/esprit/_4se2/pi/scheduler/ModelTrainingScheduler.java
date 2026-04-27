package tn.esprit._4se2.pi.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.repositories.SponsoredClickRepository;
import tn.esprit._4se2.pi.services.Sponsor.BiddingService;
import tn.esprit._4se2.pi.services.Sponsor.ModelTrainingService;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ModelTrainingScheduler {

    private final ModelTrainingService modelTrainingService;
    private final BiddingService biddingService;
    private final SponsoredClickRepository clickRepository;

    @Scheduled(cron = "0 0 2 * * *")
    public void trainModelsDaily() {
        log.info("🤖 Démarrage de l'entraînement quotidien du modèle IA");
        long startTime = System.currentTimeMillis();

        try {
            modelTrainingService.trainModel();
            biddingService.resetDailyCounters();

            long newInteractions = clickRepository.countByClickedAtAfter(LocalDateTime.now().minusDays(1));
            double globalCTR = clickRepository.getGlobalCTR() != null ? clickRepository.getGlobalCTR() : 0;

            log.info("📊 Nouvelles interactions: {}", newInteractions);
            log.info("📈 CTR Global: {:.2f}%", globalCTR * 100);
            log.info("✅ Entraînement terminé en {} ms", System.currentTimeMillis() - startTime);

        } catch (Exception e) {
            log.error("❌ Erreur lors de l'entraînement: {}", e.getMessage());
        }
    }
}