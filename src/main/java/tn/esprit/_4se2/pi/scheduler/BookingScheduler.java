package tn.esprit._4se2.pi.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.entities.Booking;
import tn.esprit._4se2.pi.repositories.BookingRepository;
import tn.esprit._4se2.pi.services.Booking.IBookingService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduler dédié au module Booking/SportSpace.
 *
 * Logique métier : toutes les minutes, on cherche les réservations
 * dont le statut est encore "CONFIRMED" mais dont l'heure de fin est
 * déjà passée → elles sont automatiquement basculées en "COMPLETED".
 *
 * Pourquoi c'est utile ?
 *  - Libère les créneaux visuellement dans le frontend.
 *  - Permet à l'utilisateur de laisser un feedback (un feedback ne peut
 *    être soumis que pour une réservation COMPLETED).
 *  - Évite de laisser des données incohérentes en base.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class BookingScheduler {

    private final BookingRepository bookingRepository;
    private final IBookingService bookingService;

    /**
     * S'exécute toutes les minutes (60 000 ms).
     * fixedDelay = attend la fin de l'exécution précédente avant de relancer.
     */
    @Scheduled(fixedDelay = 60_000)
    @Transactional
    public void completeExpiredBookings() {
        bookingService.sendBookingConfirmationReminders();
        bookingService.cancelExpiredUnconfirmedBookings();

        LocalDateTime now = LocalDateTime.now();
        log.info("[Scheduler] Vérification des réservations expirées à {}", now);

        // On récupère uniquement les réservations CONFIRMED dont la fin est passée.
        List<Booking> confirmed = bookingRepository.findByStatus("CONFIRMED");

        List<Booking> toComplete = confirmed.stream()
                .filter(b -> b.getEndTime() != null && b.getEndTime().isBefore(now)) // Filtrer les réservations expirées
                .toList();

        if (toComplete.isEmpty()) {
            log.info("[Scheduler] Aucune réservation expirée trouvée.");
            return;
        }

        // On met à jour le statut en mémoire pour toutes les entités concernées,
        // puis on fait un seul appel saveAll → un seul batch SQL UPDATE.
        toComplete.forEach(b -> b.setStatus("COMPLETED"));

        // Utiliser flush() pour forcer l'enregistrement des modifications dans la base
        bookingRepository.saveAll(toComplete);
        bookingRepository.flush();  // Force l'écriture immédiate dans la base de données

        log.info("[Scheduler] {} réservation(s) passée(s) en COMPLETED.", toComplete.size());
    }
}
