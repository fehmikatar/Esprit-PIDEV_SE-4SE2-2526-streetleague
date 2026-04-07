package tn.esprit._4se2.pi.services.Feedback;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.dto.Feedback.FeedbackRequest;
import tn.esprit._4se2.pi.dto.Feedback.FeedbackResponse;
import tn.esprit._4se2.pi.entities.Booking;
import tn.esprit._4se2.pi.entities.Feedback;
import tn.esprit._4se2.pi.entities.SportSpace;
import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.mappers.FeedbackMapper;
import tn.esprit._4se2.pi.repositories.BookingRepository;
import tn.esprit._4se2.pi.repositories.FeedbackRepository;
import tn.esprit._4se2.pi.repositories.SportSpaceRepository;
import tn.esprit._4se2.pi.repositories.UserRepository;

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

    @Override
    public FeedbackResponse createFeedback(FeedbackRequest request) {
        log.info("Creating feedback for sport space: {}", request.getSportSpaceId()); // ✅

        Booking booking = bookingRepository.findByIdAndUserId(request.getBookingId(), request.getUserId())
                .orElseThrow(() -> new RuntimeException("Booking not found for this user"));

        if (!"CONFIRMED".equalsIgnoreCase(booking.getStatus())) {
            throw new RuntimeException("Only confirmed bookings can receive feedback");
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

        Feedback feedback = feedbackMapper.toEntity(request);

        Feedback savedFeedback = feedbackRepository.save(feedback);

        log.info("Feedback created successfully with id: {}", savedFeedback.getId());

        return enrichResponse(savedFeedback);
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
                .map(this::enrichResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackResponse> getFeedbacksByUserId(Long userId) {
        log.info("Fetching feedbacks for user: {}", userId);
        return feedbackRepository.findByUserId(userId)
                .stream()
                .map(this::enrichResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackResponse> getApprovedFeedbacks() {
        log.info("Fetching approved feedbacks");
        return feedbackRepository.findByStatus("APPROVED")
                .stream()
                .map(this::enrichResponse)
                .collect(Collectors.toList());
    }

    @Override
    public FeedbackResponse updateFeedback(Long id, FeedbackRequest request) {
        log.info("Updating feedback with id: {}", id);

        Feedback feedback = feedbackRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));

        feedbackMapper.updateEntity(request, feedback);
        Feedback updatedFeedback = feedbackRepository.save(feedback);
        log.info("Feedback updated successfully with id: {}", id);

        return enrichResponse(updatedFeedback);
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

    private String buildUserName(User user) {
        String firstName = user.getFirstName() == null ? "" : user.getFirstName().trim();
        String lastName = user.getLastName() == null ? "" : user.getLastName().trim();
        String fullName = (firstName + " " + lastName).trim();
        return fullName.isBlank() ? user.getEmail() : fullName;
    }
}
