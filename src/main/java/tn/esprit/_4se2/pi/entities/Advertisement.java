package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Entity
@Table(name = "advertisements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Advertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String companyName;

    String contactEmail;

    Double amount;

    @Column(length = 500)
    String counterparts; // e.g. "Banner, Logo, Field Ad"

    @Column(columnDefinition = "LONGTEXT")
    String bannerUrl; // Main banner image

    @Column(columnDefinition = "LONGTEXT")
    String logoUrl; // Square logo

    // Optional links to specific elements
    Long competitionId;
    Long matchId;
    Long teamId;

    // Analytics
    @Builder.Default
    Integer impressions = 0;

    @Builder.Default
    Integer clicks = 0;

    @Builder.Default
    Boolean isActive = true;

    LocalDateTime startDate;
    LocalDateTime endDate;
    LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}
