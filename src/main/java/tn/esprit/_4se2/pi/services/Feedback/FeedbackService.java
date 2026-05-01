package tn.esprit._4se2.pi.services.Feedback;


import tn.esprit._4se2.pi.entities.Notification;
import tn.esprit._4se2.pi.repositories.NotificationRepository;
import tn.esprit._4se2.pi.dto.Feedback.ToxicityResult;
import tn.esprit._4se2.pi.services.Feedback.ToxicityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit._4se2.pi.dto.Feedback.FeedbackRequest;
import tn.esprit._4se2.pi.dto.Feedback.FeedbackResponse;
import tn.esprit._4se2.pi.entities.Booking;
import tn.esprit._4se2.pi.entities.Feedback;
import tn.esprit._4se2.pi.entities.SportSpace;
import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.Enum.Role;
import tn.esprit._4se2.pi.mappers.FeedbackMapper;
import tn.esprit._4se2.pi.repositories.BookingRepository;
import tn.esprit._4se2.pi.repositories.FeedbackRepository;
import tn.esprit._4se2.pi.repositories.SportSpaceRepository;
import tn.esprit._4se2.pi.repositories.UserRepository;

import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FeedbackService implements IFeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final FeedbackMapper feedbackMapper;
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final SportSpaceRepository sportSpaceRepository;
    private final ToxicityService toxicityService;
    private final NotificationRepository notificationRepository;



    @Override

    public FeedbackResponse createFeedback(FeedbackRequest request) {
        log.info("Creating feedback for sport space: {}", request.getSportSpaceId());

        Booking booking = bookingRepository.findByIdAndUserId(request.getBookingId(), request.getUserId())
                .orElseThrow(() -> new RuntimeException("Booking not found for this user"));

        if (!"CONFIRMED".equalsIgnoreCase(booking.getStatus())
                && !"COMPLETED".equalsIgnoreCase(booking.getStatus())) {
            throw new RuntimeException("Only confirmed or completed bookings can receive feedback");
        }

        if (!booking.getSportSpaceId().equals(request.getSportSpaceId())) {
            throw new RuntimeException("This booking does not belong to the selected sport space");
        }

        if (booking.getEndTime() == null || booking.getEndTime().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Feedback is allowed only after the match has finished");
        }

        if (feedbackRepository.findByBookingId(request.getBookingId()).isPresent()) {
            throw new RuntimeException("Feedback already exists for this booking");
        }

        // ── Détection toxicité IA ─────────────────────────────────────────────
        String comment = request.getComment();
        final boolean isToxic;
        if (comment != null && !comment.isBlank()) {
            ToxicityResult toxicity = toxicityService.analyze(comment);
            isToxic = "TOXIC".equalsIgnoreCase(toxicity.getLabel()) || toxicity.isToxic();
        } else {
            isToxic = false;
        }

        final String ownerNotificationTitle = isToxic ? "Feedback toxique reçu" : "Nouveau feedback reçu";
        final String ownerNotificationType = isToxic ? "TOXIC_FEEDBACK" : "NEW_FEEDBACK";
        final String ownerVisibleComment = comment != null ? comment : "Aucun";
        // ─────────────────────────────────────────────────────────────────────

        Feedback feedback = feedbackMapper.toEntity(request);
        feedback.setToxic(isToxic);
        feedback.setCensoredComment(censorComment(comment));
        Feedback savedFeedback = feedbackRepository.save(feedback);
        log.info("Feedback created successfully with id: {}", savedFeedback.getId());

        // ── Notifier l'owner : nouveau feedback ───────────────────────────────
        sportSpaceRepository.findById(request.getSportSpaceId()).ifPresent(space -> {
            String stars = "⭐".repeat(request.getRating());
            Notification notif = Notification.builder()
                    .userId(space.getFieldOwnerId())
                    .title(ownerNotificationTitle)
                    .message("Vous avez reçu un nouveau feedback " + stars
                            + " sur votre terrain \"" + space.getName() + "\". "
                            + "Commentaire : \""
                            + ownerVisibleComment
                            + "\"")
                    .type(ownerNotificationType)
                    .createdAt(LocalDateTime.now())
                    .build();
            notificationRepository.save(notif);
            log.info("[Notification] Nouveau feedback envoyé à l'owner #{}", space.getFieldOwnerId());
        });
        // ─────────────────────────────────────────────────────────────────────

        return toPlayerVisibleResponse(savedFeedback);
    }
    @Override
    @Transactional(readOnly = true)
    public FeedbackResponse getFeedbackById(Long id) {
        log.info("Fetching feedback with id: {}", id);
        return feedbackRepository.findById(id)
                .map(this::enrichResponse)
                .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackResponse> getAllFeedbacks() {
        log.info("Fetching all feedbacks");
        return feedbackRepository.findAll()
                .stream()
                .map(this::enrichResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackResponse> getFeedbacksBySportSpaceId(Long sportSpaceId) {
        log.info("Fetching feedbacks for sport space: {}", sportSpaceId);
        return feedbackRepository.findBySportSpaceIdAndStatus(sportSpaceId, "APPROVED")
                .stream()
                .map(this::toPlayerVisibleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackResponse> getFeedbacksForPlayer(Long sportSpaceId) {
        log.info("Fetching player-visible feedbacks for sport space: {}", sportSpaceId);
        return feedbackRepository.findBySportSpaceIdAndStatus(sportSpaceId, "APPROVED")
                .stream()
                .map(this::toPlayerVisibleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackResponse> getFeedbacksForOwnerSpace(Long sportSpaceId, String ownerEmail) {
        log.info("Fetching owner feedbacks for sport space: {} and owner {}", sportSpaceId, ownerEmail);

        SportSpace sportSpace = sportSpaceRepository.findById(sportSpaceId)
                .orElseThrow(() -> new RuntimeException("SportSpace not found"));

        Long ownerId = userRepository.findByEmail(ownerEmail)
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("Owner not found with email: " + ownerEmail));

        Role requesterRole = userRepository.findByEmail(ownerEmail)
                .map(User::getRole)
                .orElseThrow(() -> new RuntimeException("Owner not found with email: " + ownerEmail));

        if (requesterRole != Role.ROLE_ADMIN && !sportSpace.getFieldOwnerId().equals(ownerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous n'êtes pas autorisé à consulter ces feedbacks");
        }

        return feedbackRepository.findBySportSpaceId(sportSpaceId)
                .stream()
                .map(this::enrichResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackResponse> getFeedbacksByUserId(Long userId) {
        log.info("Fetching feedbacks for user: {}", userId);
        return feedbackRepository.findByUserId(userId)
                .stream()
                .map(this::toPlayerVisibleResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackResponse> getApprovedFeedbacks() {
        log.info("Fetching approved feedbacks");
        return feedbackRepository.findByStatus("APPROVED")
                .stream()
                .map(this::toPlayerVisibleResponse)
                .collect(Collectors.toList());
    }

    @Override
    public FeedbackResponse updateFeedback(Long id, FeedbackRequest request) {
        log.info("Updating feedback with id: {}", id);

        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));

        feedbackMapper.updateEntity(request, feedback);
        feedback.setCensoredComment(censorComment(request.getComment()));
        Feedback updatedFeedback = feedbackRepository.save(feedback);
        log.info("Feedback updated successfully with id: {}", id);

        return toPlayerVisibleResponse(updatedFeedback);
    }

    @Override
    public void deleteFeedback(Long id) {
        log.info("Deleting feedback with id: {}", id);

        if (!feedbackRepository.existsById(id)) {
            throw new RuntimeException("Feedback not found with id: " + id);
        }

        feedbackRepository.deleteById(id);
        log.info("Feedback deleted successfully with id: {}", id);
    }

    @Override
    public void approveFeedback(Long id) {
        log.info("Approving feedback with id: {}", id);

        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));

        feedback.setStatus("APPROVED");
        feedback.setApprovedAt(LocalDateTime.now());
        feedbackRepository.save(feedback);
        log.info("Feedback approved successfully with id: {}", id);
    }

    @Override
    public FeedbackResponse replyToFeedback(Long feedbackId, Long ownerId, String reply) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found: " + feedbackId));

        SportSpace space = sportSpaceRepository.findById(feedback.getSportSpaceId())
                .orElseThrow(() -> new RuntimeException("SportSpace not found"));

        if (!space.getFieldOwnerId().equals(ownerId)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à répondre à ce feedback");
        }

        feedback.setOwnerReply(reply);
        feedback.setOwnerRepliedAt(LocalDateTime.now());
        feedbackRepository.save(feedback);

        // Notifier l'utilisateur
        Notification notif = Notification.builder()
                .userId(feedback.getUserId())
                .title(" L'owner a répondu à votre feedback")
                .message("Le propriétaire de \"" + space.getName() + "\" a répondu : \"" + reply + "\"")
                .type("OWNER_REPLY")
                .createdAt(LocalDateTime.now())
                .build();
        notificationRepository.save(notif);

        return enrichResponse(feedback);
    }

    @Override
    public void ownerDeleteFeedback(Long feedbackId, Long ownerId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new RuntimeException("Feedback not found: " + feedbackId));

        SportSpace space = sportSpaceRepository.findById(feedback.getSportSpaceId())
                .orElseThrow(() -> new RuntimeException("SportSpace not found"));

        if (!space.getFieldOwnerId().equals(ownerId)) {
            throw new RuntimeException("Vous n'êtes pas autorisé à supprimer ce feedback");
        }

        feedbackRepository.delete(feedback);
        log.info("[Owner] Feedback #{} supprimé par owner #{}", feedbackId, ownerId);
    }

    @Override
    public void ownerDeleteFeedback(Long feedbackId, String ownerEmail) {
        Long ownerId = userRepository.findByEmail(ownerEmail)
                .map(User::getId)
                .orElseThrow(() -> new RuntimeException("Owner not found with email: " + ownerEmail));

        ownerDeleteFeedback(feedbackId, ownerId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackResponse> getFeedbacksForOwner(Long ownerId) {
        List<Long> spaceIds = sportSpaceRepository.findByFieldOwnerId(ownerId)
                .stream()
                .map(SportSpace::getId)
                .collect(Collectors.toList());

        return feedbackRepository.findBySportSpaceIdIn(spaceIds)
                .stream()
                .map(this::enrichResponse)
                .collect(Collectors.toList());
    }

    private FeedbackResponse enrichResponse(Feedback feedback) {
        FeedbackResponse response = feedbackMapper.toResponse(feedback);

        userRepository.findById(feedback.getUserId())
                .map(this::buildUserName)
                .ifPresent(response::setUserName);

        sportSpaceRepository.findById(feedback.getSportSpaceId())
                .map(SportSpace::getName)
                .ifPresent(response::setSportSpaceName);

        return response;
    }

    private FeedbackResponse toPlayerVisibleResponse(Feedback feedback) {
        FeedbackResponse response = enrichResponse(feedback);

        String visibleComment = feedback.isToxic()
                ? feedback.getCensoredComment()
                : feedback.getComment();

        response.setComment(visibleComment);
        response.setCensoredComment(visibleComment);
        return response;
    }

    private String censorComment(String comment) {
        if (comment == null) {
            return null;
        }

        StringBuilder censored = new StringBuilder(comment.length());
        for (char character : comment.toCharArray()) {
            censored.append(Character.isWhitespace(character) ? character : '*');
        }
        return censored.toString();
    }

    private String buildUserName(User user) {
        String firstName = user.getFirstName() == null ? "" : user.getFirstName().trim();
        String lastName = user.getLastName() == null ? "" : user.getLastName().trim();
        String fullName = (firstName + " " + lastName).trim();
        return fullName.isBlank() ? user.getEmail() : fullName;
    }
}
