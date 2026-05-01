package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Entity
@Table(name = "sponsored_clicks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SponsoredClick {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    Product product;

    @Column(name = "is_clicked")
    @Builder.Default
    Boolean isClicked = false;

    @Column(name = "is_purchased")
    @Builder.Default
    Boolean isPurchased = false;

    @Column(name = "click_duration")
    Integer clickDuration;

    @Column(name = "clicked_at")
    LocalDateTime clickedAt;

    @Column(name = "sponsored_position")
    Integer sponsoredPosition;

    @Column(name = "session_id")
    String sessionId;

    @Column(name = "bid_amount")
    Double bidAmount;

    @Column(name = "relevance_score")
    Double relevanceScore;
}