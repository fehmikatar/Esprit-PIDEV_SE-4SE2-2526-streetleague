package tn.esprit._4se2.pi.services.Booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.dto.Booking.BookingRequest;
import tn.esprit._4se2.pi.dto.Booking.BookingResponse;
import tn.esprit._4se2.pi.dto.Notification.NotificationRequest;
import tn.esprit._4se2.pi.entities.Booking;
import tn.esprit._4se2.pi.entities.SportSpace;
import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.exception.ConflictException;
import tn.esprit._4se2.pi.mappers.BookingMapper;
import tn.esprit._4se2.pi.repositories.BookingRepository;
import tn.esprit._4se2.pi.repositories.SportSpaceRepository;
import tn.esprit._4se2.pi.repositories.UserRepository;
import tn.esprit._4se2.pi.services.Notification.INotificationService;
import tn.esprit._4se2.pi.services.WebSocket.WebSocketNotificationService;

import java.time.LocalDateTime;
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
    private final INotificationService notificationService;
    private final UserRepository userRepository;
    private final SportSpaceRepository sportSpaceRepository;

    @Override
    public BookingResponse createBooking(BookingRequest request) {
        log.info("Creating booking for user: {} and sport space: {}", request.getUserId(), request.getSportSpaceId());

        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new RuntimeException("End time must be after start time");
        }

        checkForBookingConflict(request.getSportSpaceId(), request.getStartTime(), request.getEndTime());

        Booking booking = bookingMapper.toEntity(request);
        booking.setUserId(request.getUserId());
        booking.setSportSpaceId(request.getSportSpaceId());

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created successfully with id: {}", savedBooking.getId());

        String date = savedBooking.getStartTime().toLocalDate().toString();
        String time = savedBooking.getStartTime().toLocalTime().toString().substring(0, 5);
        String sportSpaceName = sportSpaceRepository.findById(savedBooking.getSportSpaceId())
                .map(SportSpace::getName)
                .orElse("Terrain");

        persistNotification(
                request.getUserId(),
                "Reservation confirmee",
                String.format("Votre reservation pour \"%s\" le %s a %s a ete confirmee.", sportSpaceName, date, time),
                "BOOKING"
        );

        try {
            webSocketNotificationService.sendReservationNotification(
                    request.getUserId(),
                    "Reservation confirmee",
                    "Votre reservation a ete confirmee avec succes.",
                    sportSpaceName,
                    date,
                    time
            );
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de la notification WebSocket", e);
        }

        return enrichResponse(savedBooking);
    }

    private void checkForBookingConflict(Long sportSpaceId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Booking> existingBookings = bookingRepository.findBySportSpaceId(sportSpaceId)
                .stream()
                .filter(booking -> "CONFIRMED".equals(booking.getStatus()))
                .toList();

        log.info("Verification de conflit pour terrain {}: {} reservations confirmees trouvees",
                sportSpaceId, existingBookings.size());
        log.info("  Nouvelle reservation demandee:");
        log.info("    - Date/Heure debut: {}", startTime);
        log.info("    - Date/Heure fin: {}", endTime);
        log.info("    - Duree: {} heures",
                java.time.temporal.ChronoUnit.MINUTES.between(startTime, endTime) / 60.0);

        for (Booking existing : existingBookings) {
            long durationMinutes = java.time.temporal.ChronoUnit.MINUTES.between(existing.getStartTime(), existing.getEndTime());
            long durationHours = durationMinutes / 60;

            log.debug("  Reservation existante ID {}:", existing.getId());
            log.debug("    - Date/Heure debut: {}", existing.getStartTime());
            log.debug("    - Date/Heure fin: {}", existing.getEndTime());
            log.debug("    - Duree: {} heures", durationHours);

            if (startTime.isBefore(existing.getEndTime()) && endTime.isAfter(existing.getStartTime())) {
                log.warn("Conflit detecte pour terrain {}!", sportSpaceId);
                log.warn("  Nouvelle tentative: {} a {}", startTime, endTime);
                log.warn("  Reservation existante (ID {}): {} a {}",
                        existing.getId(), existing.getStartTime(), existing.getEndTime());

                String conflictMessage = String.format(
                        "Ce creneau est deja reserve. Vous demandez: %s a %s (duree: %.1f heure(s)). Conflit avec: %s a %s",
                        startTime,
                        endTime,
                        java.time.temporal.ChronoUnit.MINUTES.between(startTime, endTime) / 60.0,
                        existing.getStartTime(),
                        existing.getEndTime()
                );
                log.warn("  Message: {}", conflictMessage);
                throw new ConflictException(conflictMessage);
            }
        }

        log.info("Aucun conflit - Creneau disponible pour terrain {}", sportSpaceId);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBookingById(Long id) {
        log.info("Fetching booking with id: {}", id);
        return bookingRepository.findById(id)
                .map(this::enrichResponse)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getAllBookings() {
        log.info("Fetching all bookings");
        return bookingRepository.findAll()
                .stream()
                .map(this::enrichResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByUserId(Long userId) {
        log.info("Fetching bookings for user: {}", userId);
        return bookingRepository.findByUserId(userId)
                .stream()
                .map(this::enrichResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsBySportSpaceId(Long sportSpaceId) {
        log.info("Fetching bookings for sport space: {}", sportSpaceId);
        return bookingRepository.findBySportSpaceId(sportSpaceId)
                .stream()
                .map(this::enrichResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByStatus(String status) {
        log.info("Fetching bookings with status: {}", status);
        return bookingRepository.findByStatus(status)
                .stream()
                .map(this::enrichResponse)
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

        return enrichResponse(updatedBooking);
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

        String sportSpaceName = sportSpaceRepository.findById(booking.getSportSpaceId())
                .map(SportSpace::getName)
                .orElse("Terrain");

        persistNotification(
                booking.getUserId(),
                "Reservation annulee",
                String.format("Votre reservation pour \"%s\" a ete annulee.", sportSpaceName),
                "BOOKING"
        );

        try {
            webSocketNotificationService.sendCancellationNotification(
                    booking.getUserId(),
                    "Reservation annulee",
                    "Votre reservation a ete annulee.",
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

    private BookingResponse enrichResponse(Booking booking) {
        BookingResponse response = bookingMapper.toResponse(booking);

        userRepository.findById(booking.getUserId())
                .ifPresent(user -> {
                    response.setUserName(buildUserName(user));
                    response.setUserEmail(user.getEmail());
                    response.setUserPhone(user.getPhone());
                });

        sportSpaceRepository.findById(booking.getSportSpaceId())
                .map(SportSpace::getName)
                .ifPresent(response::setSportSpaceName);

        return response;
    }

    private String buildUserName(User user) {
        String firstName = user.getFirstName() == null ? "" : user.getFirstName().trim();
        String lastName = user.getLastName() == null ? "" : user.getLastName().trim();
        String fullName = (firstName + " " + lastName).trim();
        return fullName.isBlank() ? user.getEmail() : fullName;
    }

    private void persistNotification(Long userId, String title, String message, String type) {
        try {
            notificationService.createNotification(
                    NotificationRequest.builder()
                            .userId(userId)
                            .title(title)
                            .message(message)
                            .type(type)
                            .build()
            );
        } catch (Exception e) {
            log.error("Erreur lors de la persistance de la notification pour l'utilisateur {}", userId, e);
        }
    }
}
