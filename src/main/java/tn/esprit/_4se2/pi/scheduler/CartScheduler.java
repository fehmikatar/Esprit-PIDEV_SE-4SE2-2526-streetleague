package tn.esprit._4se2.pi.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.repositories.CartRepository;

import java.time.LocalDateTime;

@Component
@Slf4j
@RequiredArgsConstructor
public class CartScheduler {

    private final CartRepository cartRepository;

    /**
     * Chaque heure, vérifie les paniers actifs qui n'ont pas été modifiés depuis plus de 24 heures
     * et les marque comme ABANDONNÉS.
     */
    @Scheduled(cron = "0 0 * * * *") // S'exécute au début de chaque heure
    @Transactional
    public void cleanupAbandonedCarts() {
        log.info("Début du nettoyage des paniers abandonnés...");
        LocalDateTime threshold = LocalDateTime.now().minusHours(24);
        int updatedCount = cartRepository.markCartsAsAbandoned(threshold);
        
        if (updatedCount > 0) {
            log.info("{} paniers ont été marqués comme ABANDONNÉS car inactifs depuis {}", updatedCount, threshold);
        } else {
            log.info("Aucun panier abandonné détecté.");
        }
    }
}
