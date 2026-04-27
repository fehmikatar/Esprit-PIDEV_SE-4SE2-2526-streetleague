package tn.esprit._4se2.pi.entities;
import jakarta.persistence.*;
import lombok.*;
import tn.esprit._4se2.pi.Enum.MemberStatus;
import tn.esprit._4se2.pi.Enum.TeamRole;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "team_members",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_active_team_member",
                        columnNames = {"team_id", "user_id", "status"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private TeamRole teamRole = TeamRole.PLAYER;

    private LocalDateTime joinedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private MemberStatus status = MemberStatus.ACTIVE;
}