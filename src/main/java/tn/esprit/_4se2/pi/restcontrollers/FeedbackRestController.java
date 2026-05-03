package tn.esprit._4se2.pi.restcontrollers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.dto.Feedback.FeedbackRequest;
import tn.esprit._4se2.pi.dto.Feedback.FeedbackResponse;
import tn.esprit._4se2.pi.dto.Feedback.FeedbackSummaryRequest;
import tn.esprit._4se2.pi.dto.Feedback.FeedbackSummaryResponse;
import tn.esprit._4se2.pi.services.Feedback.IFeedbackService;
import tn.esprit._4se2.pi.services.Feedback.FeedbackSummaryService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedbacks")
@RequiredArgsConstructor
@Tag(name = "Feedbacks API", description = "Gestion des feedbacks et détection de toxicité")
public class FeedbackRestController {

    private final IFeedbackService feedbackService;
    private final FeedbackSummaryService feedbackSummaryService;

    @PostMapping
    public ResponseEntity<FeedbackResponse> createFeedback(@Valid @RequestBody FeedbackRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(feedbackService.createFeedback(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FeedbackResponse> getFeedbackById(@PathVariable Long id) {
        return ResponseEntity.ok(feedbackService.getFeedbackById(id));
    }

    @GetMapping
    public ResponseEntity<List<FeedbackResponse>> getAllFeedbacks() {
        return ResponseEntity.ok(feedbackService.getAllFeedbacks());
    }

    @GetMapping("/sport-space/{sportSpaceId}")
    public ResponseEntity<List<FeedbackResponse>> getFeedbacksBySportSpaceId(@PathVariable Long sportSpaceId) {
        return ResponseEntity.ok(feedbackService.getFeedbacksForPlayer(sportSpaceId));
    }

    @GetMapping("/space/{sportSpaceId}/player")
    public ResponseEntity<List<FeedbackResponse>> getFeedbacksForPlayer(@PathVariable Long sportSpaceId) {
        return ResponseEntity.ok(feedbackService.getFeedbacksForPlayer(sportSpaceId));
    }

    @GetMapping("/space/{sportSpaceId}/owner")
    public ResponseEntity<List<FeedbackResponse>> getFeedbacksForOwner(
            @PathVariable Long sportSpaceId,
            Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(feedbackService.getFeedbacksForOwnerSpace(sportSpaceId, authentication.getName()));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FeedbackResponse>> getFeedbacksByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(feedbackService.getFeedbacksByUserId(userId));
    }

    @GetMapping("/approved")
    public ResponseEntity<List<FeedbackResponse>> getApprovedFeedbacks() {
        return ResponseEntity.ok(feedbackService.getApprovedFeedbacks());
    }

    @PostMapping("/summary")
    public ResponseEntity<FeedbackSummaryResponse> summarizeFeedbacks(
            @Valid @RequestBody FeedbackSummaryRequest request) {
        String summary = feedbackSummaryService.summarizeComments(request.getComments());

        return ResponseEntity.ok(
                FeedbackSummaryResponse.builder()
                        .summary(summary)
                        .build()
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<FeedbackResponse> updateFeedback(
            @PathVariable Long id,
            @Valid @RequestBody FeedbackRequest request) {
        return ResponseEntity.ok(feedbackService.updateFeedback(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable Long id) {
        feedbackService.deleteFeedback(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/approve")
    public ResponseEntity<Void> approveFeedback(@PathVariable Long id) {
        feedbackService.approveFeedback(id);
        return ResponseEntity.noContent().build();
    }

    // ── Endpoints Owner ───────────────────────────────────────────────────

    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<FeedbackResponse>> getFeedbacksForOwner(
            @PathVariable Long ownerId) {
        return ResponseEntity.ok(feedbackService.getFeedbacksForOwner(ownerId));
    }

    @PostMapping("/{id}/reply")
    public ResponseEntity<FeedbackResponse> replyToFeedback(
            @PathVariable Long id,
            @RequestParam Long ownerId,
            @RequestBody Map<String, String> body) {
        String reply = body.get("reply");
        if (reply == null || reply.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(feedbackService.replyToFeedback(id, ownerId, reply));
    }

    @DeleteMapping("/{id}/owner")
    public ResponseEntity<Void> ownerDeleteFeedback(
            @PathVariable Long id,
            @RequestParam Long ownerId) {
        feedbackService.ownerDeleteFeedback(id, ownerId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/owner/me")
    public ResponseEntity<Void> ownerDeleteFeedbackAsAuthenticatedOwner(
            @PathVariable Long id,
            Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        feedbackService.ownerDeleteFeedback(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
