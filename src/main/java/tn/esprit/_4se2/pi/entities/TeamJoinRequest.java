package tn.esprit._4se2.pi.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import tn.esprit._4se2.pi.Enum.JoinRequestStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "team_join_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})  // ← NOUVEAU
public class TeamJoinRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)  // ← Changé de LAZY à EAGER
    @JoinColumn(name = "team_id", nullable = false)
    @JsonIgnoreProperties({"members", "joinRequests", "createdBy"})  // ← NOUVEAU
    private Team team;

    @ManyToOne(fetch = FetchType.EAGER)  // ← Changé de LAZY à EAGER
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "passwordHash"})  // ← NOUVEAU
    private User user;

    @Column(length = 500)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private JoinRequestStatus status = JoinRequestStatus.PENDING;

    private LocalDateTime createdAt;

    private LocalDateTime reviewedAt;

    @ManyToOne(fetch = FetchType.EAGER)  // ← Changé de LAZY à EAGER
    @JoinColumn(name = "reviewed_by_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "passwordHash"})  // ← NOUVEAU
    private User reviewedBy;
}