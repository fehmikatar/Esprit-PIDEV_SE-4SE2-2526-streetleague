package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit._4se2.pi.Enum.JoinRequestStatus;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "team_join_requests",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uq_pending_join_request",
            columnNames = {"team_id", "user_id", "status"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamJoinRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 500)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private JoinRequestStatus status = JoinRequestStatus.PENDING;

    private LocalDateTime createdAt;

    private LocalDateTime reviewedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_id")
    private User reviewedBy;
}
