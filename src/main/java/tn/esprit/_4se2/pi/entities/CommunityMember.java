package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;
import lombok.*;
<<<<<<< Updated upstream
import tn.esprit._4se2.pi.Enum.CommunityMemberRole;
=======
>>>>>>> Stashed changes

import java.time.LocalDateTime;

@Entity
@Table(
        name = "community_members",
<<<<<<< Updated upstream
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_community_member", columnNames = {"community_id", "user_id"})
        }
=======
        uniqueConstraints = @UniqueConstraint(columnNames = {"community_id", "user_id"})
>>>>>>> Stashed changes
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

<<<<<<< Updated upstream
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "community_id", nullable = false)
    private SportCommunity community;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private CommunityMemberRole role = CommunityMemberRole.MEMBER;
}
=======
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private LocalDateTime joinedAt;
}
>>>>>>> Stashed changes
