package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;
import lombok.*;
import tn.esprit._4se2.pi.Enum.CommunityMemberRole;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "community_members",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_community_member", columnNames = {"community_id", "user_id"})
        }
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
