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
import tn.esprit._4se2.pi.entities.TeamMember;
import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.exception.ConflictException;
import tn.esprit._4se2.pi.mappers.BookingMapper;
import tn.esprit._4se2.pi.repositories.BookingRepository;
import tn.esprit._4se2.pi.repositories.SportSpaceRepository;
import tn.esprit._4se2.pi.repositories.TeamMemberRepository;
import tn.esprit._4se2.pi.repositories.UserRepository;
import tn.esprit._4se2.pi.services.Notification.INotificationService;
import tn.esprit._4se2.pi.services.WebSocket.WebSocketNotificationService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookingService implements IBookingService {

    private static final String STATUS_PENDING_CONFIRMATION = "PENDING_CONFIRMATION";
    private static final String STATUS_REMINDER_SENT = "REMINDER_SENT";
    private static final String STATUS_CONFIRMED = "CONFIRMED";
    private static final String STATUS_CANCELLED = "CANCELLED";
    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final long MINIMUM_ADVANCE_NOTICE_HOURS = 2;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final WebSocketNotificationService webSocketNotificationService;
    private final INotificationService notificationService;
    private final UserRepository userRepository;
    private final SportSpaceRepository sportSpaceRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Override
    public BookingResponse createBooking(BookingRequest request) {
        log.info("Creating booking for user: {} and sport space: {}", request.getUserId(), request.getSportSpaceId());

        validateBookingWindow(request.getSportSpaceId(), request.getStartTime(), request.getEndTime(), null);

        Booking booking = bookingMapper.toEntity(request);
        booking.setUserId(request.getUserId());
        booking.setSportSpaceId(request.getSportSpaceId());
        booking.setStatus(STATUS_PENDING_CONFIRMATION);

        sportSpaceRepository.findById(request.getSportSpaceId())
                .ifPresent(booking::setSportSpace);

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created successfully with id: {}", savedBooking.getId());

        String sportSpaceName = resolveSportSpaceName(savedBooking.getSportSpaceId());
        String date = formatDate(savedBooking.getStartTime());
        String time = formatTime(savedBooking.getStartTime());
        String bookingConfirmationMessage = buildBookingConfirmationMessage(savedBooking);

        persistNotification(
                request.getUserId(),
                "Réservation confirmée !",
                bookingConfirmationMessage,
                "BOOKING"
        );

        try {
            webSocketNotificationService.sendReservationNotification(
                    request.getUserId(),
                    "Réservation confirmée !",
                    bookingConfirmationMessage,
                    sportSpaceName,
                    date,
                    time
            );
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de la notification WebSocket", e);
        }

        BookingResponse response = enrichResponse(savedBooking);
        response.setMessage(bookingConfirmationMessage);
        return response;
    }

    private void checkForBookingConflict(Long sportSpaceId, LocalDateTime startTime, LocalDateTime endTime, Long bookingIdToIgnore) {
        List<Booking> existingBookings = bookingRepository.findBySportSpaceId(sportSpaceId)
                .stream()
                .filter(booking -> booking.getStartTime() != null && booking.getEndTime() != null)
                .filter(booking -> isBlockingBookingStatus(booking.getStatus()))
                .filter(booking -> bookingIdToIgnore == null || !Objects.equals(bookingIdToIgnore, booking.getId()))
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
                .sorted(Comparator.comparing(Booking::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(this::enrichResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByUserEmail(String userEmail) {
        log.info("Fetching bookings for user email: {}", userEmail);

        Long userId = userRepository.findByEmail(userEmail)
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        return getBookingsByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByTeamMemberUserId(Long userId) {
        log.info("Fetching team bookings for user: {}", userId);

        Set<Long> teammateUserIds = resolveTeammateUserIds(userId);
        if (teammateUserIds.isEmpty()) {
            return List.of();
        }

        return bookingRepository.findByUserIdIn(teammateUserIds.stream().toList())
                .stream()
                .sorted(Comparator.comparing(Booking::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(this::enrichResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByTeamMemberUserEmail(String userEmail) {
        log.info("Fetching team bookings for user email: {}", userEmail);

        Long userId = userRepository.findByEmail(userEmail)
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));

        return getBookingsByTeamMemberUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByOwnerId(Long ownerId) {
        log.info("Fetching bookings for owner: {}", ownerId);

        List<Long> ownerSportSpaceIds = sportSpaceRepository.findByFieldOwnerId(ownerId)
                .stream()
                .map(SportSpace::getId)
                .toList();

        if (ownerSportSpaceIds.isEmpty()) {
            return List.of();
        }

        return bookingRepository.findBySportSpaceIdIn(ownerSportSpaceIds)
                .stream()
                .sorted((left, right) -> right.getStartTime().compareTo(left.getStartTime()))
                .map(this::enrichResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getBookingsByOwnerEmail(String ownerEmail) {
        log.info("Fetching bookings for owner email: {}", ownerEmail);

        Long ownerId = userRepository.findByEmail(ownerEmail)
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("Owner not found with email: " + ownerEmail));

        return getBookingsByOwnerId(ownerId);
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

        validateBookingWindow(booking.getSportSpaceId(), request.getStartTime(), request.getEndTime(), id);

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

        booking.setStatus(STATUS_CANCELLED);
        bookingRepository.save(booking);
        log.info("Booking cancelled successfully with id: {}", id);

        String sportSpaceName = resolveSportSpaceName(booking.getSportSpaceId());

        persistNotification(
                booking.getUserId(),
                "Réservation annulée",
                String.format("Votre réservation pour \"%s\" a été annulée.", sportSpaceName),
                "BOOKING"
        );

        try {
            webSocketNotificationService.sendCancellationNotification(
                    booking.getUserId(),
                    "Réservation annulée",
                    String.format("Votre réservation pour \"%s\" a été annulée.", sportSpaceName),
                    sportSpaceName
            );
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de la notification d'annulation WebSocket", e);
        }
    }

    @Override
    public void confirmBooking(Long id) {
        confirmBooking(id, "CONFIRMER");
    }

    @Override
    public void confirmBooking(Long id, String confirmationReply) {
        log.info("Confirming booking with id: {}", id);

        if (!isValidConfirmationReply(confirmationReply)) {
            throw new IllegalArgumentException("Veuillez confirmer votre présence en répondant OUI ou CONFIRMER.");
        }

        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking not found with id: " + id));

        if (STATUS_CONFIRMED.equalsIgnoreCase(booking.getStatus())) {
            log.info("Booking {} was already confirmed", id);
            return;
        }

        if (STATUS_CANCELLED.equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalArgumentException("Cette réservation est déjà annulée.");
        }

        if (STATUS_COMPLETED.equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalArgumentException("Cette réservation est déjà terminée.");
        }

        if (booking.getStartTime() != null && !booking.getStartTime().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cette réservation ne peut plus être confirmée.");
        }

        if (!isAwaitingConfirmation(booking.getStatus()) && !STATUS_CONFIRMED.equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalArgumentException("Cette réservation n'est pas en attente de confirmation.");
        }

        booking.setStatus(STATUS_CONFIRMED);
        bookingRepository.save(booking);
        log.info("Booking confirmed successfully with id: {}", id);

        String confirmationMessage = String.format(
                "Votre présence pour la réservation du %s à %s a bien été confirmée.",
                formatDate(booking.getStartTime()),
                formatTime(booking.getStartTime())
        );

        persistNotification(
                booking.getUserId(),
                "Présence confirmée",
                confirmationMessage,
                "BOOKING"
        );

        try {
            webSocketNotificationService.sendNotification(
                    booking.getUserId(),
                    "BOOKING_CONFIRMATION",
                    "Présence confirmée",
                    confirmationMessage
            );
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de la confirmation de présence WebSocket", e);
        }
    }

    @Override
    public void sendBookingConfirmationReminders() {
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookingsToRemind = bookingRepository.findByStatus(STATUS_PENDING_CONFIRMATION)
                .stream()
                .filter(booking -> isReminderDue(booking, now))
                .toList();

        if (bookingsToRemind.isEmpty()) {
            return;
        }

        bookingsToRemind.forEach(this::sendReminderForBooking);
    }

    @Override
    public void cancelExpiredUnconfirmedBookings() {
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookingsToCancel = Stream.concat(
                        bookingRepository.findByStatus(STATUS_PENDING_CONFIRMATION).stream(),
                        bookingRepository.findByStatus(STATUS_REMINDER_SENT).stream()
                )
                .filter(booking -> booking.getStartTime() != null && !booking.getStartTime().isAfter(now))
                .toList();

        if (bookingsToCancel.isEmpty()) {
            return;
        }

        bookingsToCancel.forEach(this::autoCancelUnconfirmedBooking);
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
                .ifPresent(sportSpace -> {
                    response.setSportSpaceName(sportSpace.getName());

                    if (response.getTotalPrice() == null) {
                        response.setTotalPrice(resolveCalculatedTotalPrice(booking, sportSpace));
                    }
                });

        return response;
    }

    private BigDecimal resolveCalculatedTotalPrice(Booking booking, SportSpace sportSpace) {
        if (booking.getStartTime() == null || booking.getEndTime() == null || sportSpace.getHourlyRate() == null) {
            return null;
        }

        long durationInMinutes = Duration.between(booking.getStartTime(), booking.getEndTime()).toMinutes();
        BigDecimal durationInHours = BigDecimal.valueOf(durationInMinutes)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

        return sportSpace.getHourlyRate()
                .multiply(durationInHours)
                .setScale(2, RoundingMode.HALF_UP);
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

    private void validateBookingWindow(Long sportSpaceId, LocalDateTime startTime, LocalDateTime endTime, Long bookingIdToIgnore) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("Les dates de réservation sont obligatoires.");
        }

        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        validateMinimumReservationNotice(sportSpaceId, startTime, endTime);
        checkForBookingConflict(sportSpaceId, startTime, endTime, bookingIdToIgnore);
    }

    private void validateMinimumReservationNotice(Long sportSpaceId, LocalDateTime startTime, LocalDateTime endTime) {
        LocalDateTime minimumAllowedStartTime = LocalDateTime.now().plusHours(MINIMUM_ADVANCE_NOTICE_HOURS);

        if (startTime.isBefore(minimumAllowedStartTime)) {
            String alternative = buildNextAvailableSlotSuggestion(sportSpaceId, startTime, endTime);
            throw new IllegalArgumentException(buildTooLateReservationMessage(alternative));
        }
    }

    private String buildTooLateReservationMessage(String alternative) {
        return String.format(
                "⛔ Réservation impossible\n" +
                        "Il n'est plus possible de réserver ce créneau. Les réservations doivent être effectuées au moins 2 heures à l'avance.\n" +
                        "Prochain créneau disponible : %s",
                alternative
        );
    }

    private String buildNextAvailableSlotSuggestion(Long sportSpaceId, LocalDateTime requestedStartTime, LocalDateTime requestedEndTime) {
        long durationInMinutes = java.time.temporal.ChronoUnit.MINUTES.between(requestedStartTime, requestedEndTime);
        LocalDateTime minimumAllowedStartTime = roundUpToNextMinute(LocalDateTime.now().plusHours(MINIMUM_ADVANCE_NOTICE_HOURS));
        LocalDateTime candidateStartTime = requestedStartTime.isAfter(minimumAllowedStartTime)
                ? requestedStartTime
                : minimumAllowedStartTime;
        LocalDateTime candidateEndTime = candidateStartTime.plusMinutes(durationInMinutes);

        List<Booking> activeBookings = bookingRepository.findBySportSpaceId(sportSpaceId)
                .stream()
                .filter(booking -> booking.getStartTime() != null && booking.getEndTime() != null)
                .filter(booking -> isBlockingBookingStatus(booking.getStatus()))
                .sorted(Comparator.comparing(Booking::getStartTime))
                .toList();

        boolean updated;
        do {
            updated = false;
            for (Booking existingBooking : activeBookings) {
                if (candidateStartTime.isBefore(existingBooking.getEndTime())
                        && candidateEndTime.isAfter(existingBooking.getStartTime())) {
                    candidateStartTime = existingBooking.getEndTime();
                    candidateEndTime = candidateStartTime.plusMinutes(durationInMinutes);
                    updated = true;
                }
            }
        } while (updated);

        return String.format(
                "%s le %s à %s",
                resolveSportSpaceName(sportSpaceId),
                formatDate(candidateStartTime),
                formatTime(candidateStartTime)
        );
    }

    private LocalDateTime roundUpToNextMinute(LocalDateTime dateTime) {
        LocalDateTime rounded = dateTime.withSecond(0).withNano(0);
        return dateTime.equals(rounded) ? rounded : rounded.plusMinutes(1);
    }

    private boolean isBlockingBookingStatus(String status) {
        return !STATUS_CANCELLED.equalsIgnoreCase(status);
    }

    private boolean isReminderDue(Booking booking, LocalDateTime now) {
        return booking.getStartTime() != null
                && !booking.getStartTime().minusHours(1).isAfter(now)
                && booking.getStartTime().isAfter(now);
    }

    private boolean isAwaitingConfirmation(String status) {
        return STATUS_PENDING_CONFIRMATION.equalsIgnoreCase(status)
                || STATUS_REMINDER_SENT.equalsIgnoreCase(status);
    }

    private boolean isValidConfirmationReply(String confirmationReply) {
        if (confirmationReply == null) {
            return false;
        }

        String normalizedReply = confirmationReply.trim();
        return "OUI".equalsIgnoreCase(normalizedReply) || "CONFIRMER".equalsIgnoreCase(normalizedReply);
    }

    private void sendReminderForBooking(Booking booking) {
        String reminderMessage = buildReminderMessage(booking);

        booking.setStatus(STATUS_REMINDER_SENT);
        bookingRepository.save(booking);

        persistNotification(
                booking.getUserId(),
                "Rappel de réservation",
                reminderMessage,
                "BOOKING"
        );

        try {
            webSocketNotificationService.sendNotification(
                    booking.getUserId(),
                    "BOOKING_REMINDER",
                    "Rappel de réservation",
                    reminderMessage
            );
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi du rappel de réservation WebSocket", e);
        }
    }

    private void autoCancelUnconfirmedBooking(Booking booking) {
        booking.setStatus(STATUS_CANCELLED);
        bookingRepository.save(booking);

        String cancellationMessage = buildAutomaticCancellationMessage(booking);
        String sportSpaceName = resolveSportSpaceName(booking.getSportSpaceId());

        persistNotification(
                booking.getUserId(),
                "Réservation annulée",
                cancellationMessage,
                "BOOKING"
        );

        try {
            webSocketNotificationService.sendCancellationNotification(
                    booking.getUserId(),
                    "Réservation annulée",
                    cancellationMessage,
                    sportSpaceName
            );
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'annulation automatique WebSocket", e);
        }
    }

    private String buildBookingConfirmationMessage(Booking booking) {
        LocalDateTime reminderDateTime = booking.getStartTime().minusHours(1);

        return String.format(
                "✅ Réservation confirmée !\n" +
                        "Terrain : %s\n" +
                        "Date : %s\n" +
                        "Heure : %s\n" +
                        "Joueur : %s\n\n" +
                        "⚠️ Rappel : vous recevrez un message de confirmation 1 heure avant le créneau (%s à %s). Sans réponse de votre part, votre réservation sera automatiquement annulée.",
                resolveSportSpaceName(booking.getSportSpaceId()),
                formatDate(booking.getStartTime()),
                formatTime(booking.getStartTime()),
                resolveUserName(booking.getUserId()),
                DATE_FORMATTER.format(reminderDateTime),
                formatTime(reminderDateTime)
        );
    }

    private String buildReminderMessage(Booking booking) {
        return String.format(
                "⏰ Rappel de réservation\n" +
                        "Bonjour %s, vous avez une réservation dans moins d'une heure :\n" +
                        "Terrain : %s\n" +
                        "Date : %s à %s\n\n" +
                        "Veuillez confirmer votre présence en répondant CONFIRMER.\n" +
                        "Sans confirmation, votre réservation sera annulée automatiquement.",
                resolveUserName(booking.getUserId()),
                resolveSportSpaceName(booking.getSportSpaceId()),
                formatDate(booking.getStartTime()),
                formatTime(booking.getStartTime())
        );
    }

    private String buildAutomaticCancellationMessage(Booking booking) {
        return String.format(
                "❌ Réservation annulée\n" +
                        "Bonjour %s, votre réservation du %s à %s a été annulée automatiquement car aucune confirmation n'a été reçue.\n" +
                        "Le créneau est maintenant disponible pour d'autres joueurs.",
                resolveUserName(booking.getUserId()),
                formatDate(booking.getStartTime()),
                formatTime(booking.getStartTime())
        );
    }

    private String resolveUserName(Long userId) {
        return userRepository.findById(userId)
                .map(this::buildUserName)
                .filter(name -> !name.isBlank())
                .orElse("joueur");
    }

    private String resolveSportSpaceName(Long sportSpaceId) {
        return sportSpaceRepository.findById(sportSpaceId)
                .map(SportSpace::getName)
                .orElse("Terrain");
    }

    private Set<Long> resolveTeammateUserIds(Long userId) {
        Set<Long> teamIds = teamMemberRepository.findByUserId(userId)
                .stream()
                .map(teamMember -> teamMember.getId() != null ? teamMember.getId().getTeamId() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (teamIds.isEmpty()) {
            return Set.of(userId);
        }

        Set<Long> teammateUserIds = new LinkedHashSet<>();
        for (Long teamId : teamIds) {
            for (TeamMember member : teamMemberRepository.findByTeamId(teamId)) {
                Long memberUserId = member.getId() != null ? member.getId().getUserId() : null;
                if (memberUserId != null) {
                    teammateUserIds.add(memberUserId);
                }
            }
        }

        teammateUserIds.add(userId);
        return teammateUserIds;
    }

    private String formatDate(LocalDateTime dateTime) {
        return DATE_FORMATTER.format(dateTime);
    }

    private String formatTime(LocalDateTime dateTime) {
        return TIME_FORMATTER.format(dateTime);
    }
}
