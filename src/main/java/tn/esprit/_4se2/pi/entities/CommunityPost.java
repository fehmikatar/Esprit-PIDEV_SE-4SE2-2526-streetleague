package tn.esprit._4se2.pi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import tn.esprit._4se2.pi.Enum.PostStatus;
import tn.esprit._4se2.pi.Enum.PostType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Community post entity — distinct from the existing general-purpose Post entity.
 * Named CommunityPost to avoid conflict.
 */
@Entity
@Table(name = "community_posts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @Column(nullable = false, length = 2000)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostType postType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PostStatus status = PostStatus.VISIBLE;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder.Default
    private int likesCount = 0;

    @JsonIgnore
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CommunityComment> comments = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<PostLike> likes = new ArrayList<>();
}
