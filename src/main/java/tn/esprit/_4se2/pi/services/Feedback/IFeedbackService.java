package tn.esprit._4se2.pi.services.Feedback;

import tn.esprit._4se2.pi.dto.FeedbackRequest;
import tn.esprit._4se2.pi.dto.FeedbackResponse;
import java.util.List;

public interface IFeedbackService {
    FeedbackResponse createFeedback(FeedbackRequest request);
    FeedbackResponse getFeedbackById(Long id);
    List<FeedbackResponse> getAllFeedbacks();
    List<FeedbackResponse> getFeedbacksBySportSpaceId(Long sportSpaceId);
    List<FeedbackResponse> getFeedbacksByUserId(Long userId);
    List<FeedbackResponse> getApprovedFeedbacks();
    FeedbackResponse updateFeedback(Long id, FeedbackRequest request);
    void deleteFeedback(Long id);
    void approveFeedback(Long id);
}