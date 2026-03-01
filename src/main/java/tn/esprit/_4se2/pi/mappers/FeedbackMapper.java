package tn.esprit._4se2.pi.mappers;

import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.dto.FeedbackRequest;
import tn.esprit._4se2.pi.dto.FeedbackResponse;
import tn.esprit._4se2.pi.entities.Feedback;
import java.time.LocalDateTime;

@Component
public class FeedbackMapper {

    public Feedback toEntity(FeedbackRequest request) {
        if (request == null) return null;

        Feedback feedback = new Feedback();
        feedback.setRating(request.getRating());
        feedback.setComment(request.getComment());
        feedback.setStatus("PENDING");
        feedback.setCreatedAt(LocalDateTime.now());
        return feedback;
    }

    public FeedbackResponse toResponse(Feedback entity) {
        if (entity == null) return null;

        return FeedbackResponse.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .sportSpaceId(entity.getSportSpaceId())
                .rating(entity.getRating())
                .comment(entity.getComment())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public void updateEntity(FeedbackRequest request, Feedback feedback) {
        if (request == null || feedback == null) return;

        feedback.setRating(request.getRating());
        feedback.setComment(request.getComment());
    }
}