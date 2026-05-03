package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "feedbacks",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_feedback_user_sport_space",
                        columnNames = {"user_id", "sport_space_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "user_id", nullable = false)
    Long userId;

    @Column(name = "sport_space_id", nullable = false)
    Long sportSpaceId;

    @Column(name = "booking_id", nullable = false)
    Long bookingId;

    @Column(nullable = false)
    Integer rating; // 1-5

    @Column(length = 1000)
    String comment;

    @Column(name = "is_toxic", nullable = false)
    @Builder.Default
    boolean isToxic = false;

    @Column(name = "censored_comment", length = 1000)
    String censoredComment;

    @Column(nullable = false)
    @Builder.Default
    String status = "PENDING"; // PENDING, APPROVED, REJECTED

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "approved_at")
    LocalDateTime approvedAt;

    @Column(length = 1000)
    String ownerReply;

    @Column(name = "owner_replied_at")
    LocalDateTime ownerRepliedAt;
}
