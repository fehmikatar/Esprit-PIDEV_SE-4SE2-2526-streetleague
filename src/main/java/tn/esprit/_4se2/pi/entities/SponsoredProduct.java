package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Entity
@Table(name = "sponsored_products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SponsoredProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "sponsor_id")
    Sponsor sponsor;

    @ManyToOne
    @JoinColumn(name = "product_id")
    Product product;

    @Column(name = "bid_amount")
    Double bidAmount;

    @Column(name = "daily_impression_limit")
    Integer dailyImpressionLimit;

    @Column(name = "daily_click_limit")
    Integer dailyClickLimit;

    @Column(name = "today_impressions")
    @Builder.Default
    Integer todayImpressions = 0;

    @Column(name = "today_clicks")
    @Builder.Default
    Integer todayClicks = 0;

    @Column(name = "is_active")
    @Builder.Default
    Boolean isActive = true;

    @Column(name = "created_at")
    LocalDateTime createdAt;
}