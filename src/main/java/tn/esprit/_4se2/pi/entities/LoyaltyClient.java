package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class LoyaltyClient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true) // suppose une entité User existante
    private User user;              // ou clientId selon votre modèle

    @ManyToOne
    @JoinColumn(name = "loyalty_tier_id")
    private LoyaltyTier loyaltyTier;

    @ManyToOne
    @JoinColumn(name = "program_id")
    private LoyaltyProgram program;

    private Integer totalPoints;
    private LocalDateTime tierValidUntil;  // si les tiers sont temporaires
    private LocalDateTime joinedAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDateTime.now();
        totalPoints = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}