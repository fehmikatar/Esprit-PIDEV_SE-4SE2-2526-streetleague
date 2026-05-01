package tn.esprit._4se2.pi.services.Feedback;

import tn.esprit._4se2.pi.dto.Feedback.FeedbackRequest;
import tn.esprit._4se2.pi.dto.Feedback.FeedbackResponse;
import java.util.List;

public interface IFeedbackService {
    FeedbackResponse createFeedback(FeedbackRequest request);
    FeedbackResponse getFeedbackById(Long id);
    List<FeedbackResponse> getAllFeedbacks();
    List<FeedbackResponse> getFeedbacksBySportSpaceId(Long sportSpaceId);
    List<FeedbackResponse> getFeedbacksForPlayer(Long sportSpaceId);
    List<FeedbackResponse> getFeedbacksForOwnerSpace(Long sportSpaceId, String ownerEmail);
    List<FeedbackResponse> getFeedbacksByUserId(Long userId);
    List<FeedbackResponse> getApprovedFeedbacks();
    FeedbackResponse updateFeedback(Long id, FeedbackRequest request);
    void deleteFeedback(Long id);
    void approveFeedback(Long id);
    FeedbackResponse replyToFeedback(Long feedbackId, Long ownerId, String reply);
    void ownerDeleteFeedback(Long feedbackId, Long ownerId);
    void ownerDeleteFeedback(Long feedbackId, String ownerEmail);
    List<FeedbackResponse> getFeedbacksForOwner(Long ownerId);
}
