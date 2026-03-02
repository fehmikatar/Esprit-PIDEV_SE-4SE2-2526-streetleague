package tn.esprit._4se2.pi.services.Feedback;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.dto.FeedbackRequest;
import tn.esprit._4se2.pi.dto.FeedbackResponse;
import tn.esprit._4se2.pi.entities.Feedback;
import tn.esprit._4se2.pi.mappers.FeedbackMapper;
import tn.esprit._4se2.pi.repositories.FeedbackRepository;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class FeedbackService implements IFeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final FeedbackMapper feedbackMapper;

    @Override
    public FeedbackResponse createFeedback(FeedbackRequest request) {
        log.info("Creating feedback for sport space: {}", request.getSportSpaceId());

        Feedback feedback = feedbackMapper.toEntity(request);
        feedback.setUserId(request.getUserId());
        feedback.setSportSpaceId(request.getSportSpaceId());

        Feedback savedFeedback = feedbackRepository.save(feedback);
        log.info("Feedback created successfully with id: {}", savedFeedback.getId());

        return feedbackMapper.toResponse(savedFeedback);
    }

    @Override
    @Transactional(readOnly = true)
    public FeedbackResponse getFeedbackById(Long id) {
        log.info("Fetching feedback with id: {}", id);
        return feedbackRepository.findById(id)
                .map(feedbackMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Feedback not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackResponse> getAllFeedbacks() {
        log.info("Fetching all feedbacks");
        return feedbackRepository.findAll()
                .stream()
                .map(feedbackMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackResponse> getFeedbacksBySportSpaceId(Long sportSpaceId) {
        log.info("Fetching feedbacks for sport space: {}", sportSpaceId);
        return feedbackRepository.findBySportSpaceId(sportSpaceId)
                .stream()
                .map(feedbackMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackResponse> getFeedbacksByUserId(Long userId) {
        log.info("Fetching feedbacks for user: {}", userId);
        return feedbackRepository.findByUserId(userId)
                .stream()
                .map(feedbackMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FeedbackResponse> getApprovedFeedbacks() {
        log.info("Fetching approved feedbacks");
        return feedbackRepository.findByStatus("APPROVED")
                .stream()
                .map(feedbackMapper::toResponse)
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

        return feedbackMapper.toResponse(updatedFeedback);
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
        feedbackRepository.save(feedback);
        log.info("Feedback approved successfully with id: {}", id);
    }
}