package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sponsors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Sponsor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    String name;

    @Column(unique = true)
    String email;

    @Column(name = "company_name")
    String companyName;

    @Column(name = "daily_budget")
    Double dailyBudget;

    @Column(name = "remaining_budget")
    Double remainingBudget;

    @Column(name = "cpc_bid") // Cost Per Click
    Double cpcBid;

    @Column(name = "target_categories")
    String targetCategories; // JSON ou IDs séparés par des virgules

    @Column(name = "is_active")
    @Builder.Default
    Boolean isActive = true;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "total_clicks")
    @Builder.Default
    Integer totalClicks = 0;

    @Column(name = "total_spent")
    @Builder.Default
    Double totalSpent = 0.0;

    @OneToMany(mappedBy = "sponsor", cascade = CascadeType.ALL)
    @Builder.Default
    List<SponsoredProduct> sponsoredProducts = new ArrayList<>();
}