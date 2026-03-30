package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Entity
@Table(name = "feedbacks")
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

    @Column(nullable = false)
    Long userId;

    @Column(nullable = false)
    Long sportSpaceId;

    @Column(nullable = false)
    Integer rating; // 1-5

    @Column(length = 1000)
    String comment;

    @Column(nullable = false)
    @Builder.Default
    String status = "PENDING"; // PENDING, APPROVED, REJECTED

    @Column(name = "created_at")
    LocalDateTime createdAt;
}