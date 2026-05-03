package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit._4se2.pi.Enum.CallStatus;
import tn.esprit._4se2.pi.Enum.CallType;

import java.time.LocalDateTime;

/**
 * Persists every team call (audio or video) for history and analytics.
 *
 * Life-cycle:
 *   INITIATED  → set at call.invite
 *   ONGOING    → set at call.answer
 *   ENDED      → set at call.end (normal hang-up)
 *   REJECTED   → set at call.reject
 *   MISSED     → set when call.end arrives before call.answer (caller cancelled)
 */
@Entity
@Table(
    name = "call_records",
    indexes = {
        @Index(name = "idx_call_room",   columnList = "room_id"),
        @Index(name = "idx_call_caller", columnList = "caller_id"),
        @Index(name = "idx_call_callee", columnList = "callee_id"),
        @Index(name = "idx_call_team",   columnList = "team_id")
    }
)
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CallRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** STOMP room id, e.g. "team_42" */
    @Column(name = "room_id", nullable = false, length = 64)
    private String roomId;

    /** Convenience FK — extracted from roomId */
    @Column(name = "team_id")
    private Long teamId;

    // ── Participants ──────────────────────────────────────────────────────────

    @Column(name = "caller_id", nullable = false)
    private Long callerId;

    @Column(name = "caller_name", length = 120)
    private String callerName;

    @Column(name = "callee_id", nullable = false)
    private Long calleeId;

    @Column(name = "callee_name", length = 120)
    private String calleeName;

    // ── Type & Status ─────────────────────────────────────────────────────────

    @Enumerated(EnumType.STRING)
    @Column(name = "call_type", nullable = false, length = 10)
    @Builder.Default
    private CallType callType = CallType.AUDIO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 12)
    @Builder.Default
    private CallStatus status = CallStatus.INITIATED;

    // ── Timing ────────────────────────────────────────────────────────────────

    @Column(name = "started_at", nullable = false)
    @Builder.Default
    private LocalDateTime startedAt = LocalDateTime.now();

    /** Set when status becomes ONGOING (call.answer received) */
    @Column(name = "answered_at")
    private LocalDateTime answeredAt;

    /** Set when status becomes ENDED, REJECTED, or MISSED */
    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    /** Duration in seconds (endedAt - answeredAt), null if call never connected */
    @Column(name = "duration_seconds")
    private Integer durationSeconds;
}
