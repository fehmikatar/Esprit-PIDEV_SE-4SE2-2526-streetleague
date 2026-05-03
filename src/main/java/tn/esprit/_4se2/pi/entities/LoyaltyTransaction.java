package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class LoyaltyTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private LoyaltyClient client;

    private Integer points;            // positif = gagné, négatif = dépensé
    private String reason;             // "achat", "bonus", "expiration"
    private String relatedEntityType;  // ex: "Order", "Promotion"
    private Long relatedEntityId;

    private LocalDateTime transactionDate;

    @PrePersist
    protected void onCreate() {
        transactionDate = LocalDateTime.now();
    }
}