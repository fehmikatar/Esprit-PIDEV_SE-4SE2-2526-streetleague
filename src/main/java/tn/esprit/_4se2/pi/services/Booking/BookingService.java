package tn.esprit._4se2.pi.services.Booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.dto.Booking.BookingRequest;
import tn.esprit._4se2.pi.dto.Booking.BookingResponse;
import tn.esprit._4se2.pi.entities.Booking;
import tn.esprit._4se2.pi.exception.ConflictException;
import tn.esprit._4se2.pi.mappers.BookingMapper;
import tn.esprit._4se2.pi.repositories.BookingRepository;
import tn.esprit._4se2.pi.services.WebSocket.WebSocketNotificationService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookingService implements IBookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final WebSocketNotificationService webSocketNotificationService;
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public BookingResponse createBooking(BookingRequest request) {
        log.info("Creating booking for user: {} and sport space: {}", request.getUserId(), request.getSportSpaceId());

        // Valider les dates
        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new RuntimeException("End time must be after start time");
        }

        // ✅ Vérifier les conflits de réservation
        checkForBookingConflict(request.getSportSpaceId(), request.getStartTime(), request.getEndTime());

        Booking booking = bookingMapper.toEntity(request);
        booking.setUserId(request.getUserId());
        booking.setSportSpaceId(request.getSportSpaceId());

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created successfully with id: {}", savedBooking.getId());

        // ✅ Envoyer une notification WebSocket au client
        try {
            String date = savedBooking.getStartTime().toLocalDate().toString();
            String time = savedBooking.getStartTime().toLocalTime().toString().substring(0, 5); // HH:mm
            String sportSpaceName = "Terrain"; // Vous pouvez récupérer le vrai nom si disponible
            
            webSocketNotificationService.sendReservationNotification(
                request.getUserId(),
                "Réservation confirmée",
                "Votre réservation a été confirmée avec succès.",
                sportSpaceName,
                date,
                time
            );
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de la notification WebSocket", e);
            // Ne pas arrêter le processus si la notification échoue
        }

        return bookingMapper.toResponse(savedBooking);
    }

    /**
     * Vérifier s'il y a un conflit de réservation pour un créneau donné
     * Vérifie la DATE, L'HEURE et LA DURÉE
     * Lance ConflictException si conflit détecté
     */
    private void checkForBookingConflict(Long sportSpaceId, LocalDateTime startTime, LocalDateTime endTime) {
        // Récupérer toutes les réservations confirmées pour ce terrain
        List<Booking> existingBookings = bookingRepository.findBySportSpaceId(sportSpaceId)
                .stream()
                .filter(b -> "CONFIRMED".equals(b.getStatus())) // Seulement les confirmées
                .toList();

        log.info("Vérification de conflit pour terrain {}: {} réservations confirmées trouvées", 
                sportSpaceId, existingBookings.size());
        log.info("  Nouvelle réservation demandée:");
        log.info("    - Date/Heure début: {} ", startTime);
        log.info("    - Date/Heure fin: {} ", endTime);
        log.info("    - Durée: {} heures", 
                java.time.temporal.ChronoUnit.MINUTES.between(startTime, endTime) / 60.0);

        // Vérifier s'il y a un chevauchement
        for (Booking existing : existingBookings) {
            // Extraire les informations pour les logs
            long durationMinutes = java.time.temporal.ChronoUnit.MINUTES.between(existing.getStartTime(), existing.getEndTime());
            long durationHours = durationMinutes / 60;
            
            log.debug("  Réservation existante ID {}:", existing.getId());
            log.debug("    - Date/Heure début: {}", existing.getStartTime());
            log.debug("    - Date/Heure fin: {}", existing.getEndTime());
            log.debug("    - Durée: {} heures", durationHours);
            
            // Chevauchement si:
            // - startTime < existing.endTime ET endTime > existing.startTime
            if (startTime.isBefore(existing.getEndTime()) && endTime.isAfter(existing.getStartTime())) {
                log.warn("❌ CONFLIT DÉTECTÉ pour terrain {}!", sportSpaceId);
                log.warn("  Nouvelle tentative: {} à {}", startTime, endTime);
                log.warn("  Réservation existante (ID {}): {} à {}", 
                        existing.getId(), existing.getStartTime(), existing.getEndTime());
                
                // Message d'erreur détaillé
                String conflictMessage = String.format(
                    "Ce créneau est déjà réservé. " +
                    "Vous demandez: %s à %s (durée: %.1f heure(s)). " +
                    "Conflit avec: %s à %s",
                    startTime, endTime, 
                    java.time.temporal.ChronoUnit.MINUTES.between(startTime, endTime) / 60.0,
                    existing.getStartTime(), existing.getEndTime()
                );
                log.warn("  Message: {}", conflictMessage);
                throw new ConflictException(conflictMessage);
            }
        }

        log.info("✅ Aucun conflit - Créneau disponible pour terrain {}", sportSpaceId);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long id) {
        log.info("Fetching booking with id: {}", id);
        return bookingRepository.findById(id)
                .map(bookingMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookings() {
        log.info("Fetching all bookings");
        return bookingRepository.findAll()
                .stream()
                .map(bookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByUserId(Long userId) {
        log.info("Fetching bookings for user: {}", userId);
        return bookingRepository.findByUserId(userId)
                .stream()
                .map(bookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsBySportSpaceId(Long sportSpaceId) {
        log.info("Fetching bookings for sport space: {}", sportSpaceId);
        return bookingRepository.findBySportSpaceId(sportSpaceId)
                .stream()
                .map(bookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByStatus(String status) {
        log.info("Fetching bookings with status: {}", status);
        return bookingRepository.findByStatus(status)
                .stream()
                .map(bookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BookingResponse updateBooking(Long id, BookingRequest request) {
        log.info("Updating booking with id: {}", id);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));

        bookingMapper.updateEntity(request, booking);
        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Booking updated successfully with id: {}", id);

        return bookingMapper.toResponse(updatedBooking);
    }

    @Override
    public void deleteBooking(Long id) {
        log.info("Deleting booking with id: {}", id);

        if (!bookingRepository.existsById(id)) {
            throw new RuntimeException("Booking not found with id: " + id);
        }

        bookingRepository.deleteById(id);
        log.info("Booking deleted successfully with id: {}", id);
    }

    @Override
    public void cancelBooking(Long id) {
        log.info("Cancelling booking with id: {}", id);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));

        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
        log.info("Booking cancelled successfully with id: {}", id);

        // ✅ Envoyer une notification WebSocket au client
        try {
            String sportSpaceName = "Terrain"; // Vous pouvez récupérer le vrai nom si disponible
            
            webSocketNotificationService.sendCancellationNotification(
                booking.getUserId(),
                "Réservation annulée",
                "Votre réservation a été annulée.",
                sportSpaceName
            );
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de la notification d'annulation WebSocket", e);
        }
    }

    @Override
    public void confirmBooking(Long id) {
        log.info("Confirming booking with id: {}", id);

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));

        booking.setStatus("CONFIRMED");
        bookingRepository.save(booking);
        log.info("Booking confirmed successfully with id: {}", id);
    }
}