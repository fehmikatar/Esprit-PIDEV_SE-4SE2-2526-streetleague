package tn.esprit._4se2.pi.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sport_communities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SportCommunity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sport_category_id", nullable = false)
    private Category sportCategory;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @JsonIgnore
    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CommunityMember> members = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "community", cascade = CascadeType.ALL)
    @Builder.Default
    private List<CommunityPost> posts = new ArrayList<>();
}
