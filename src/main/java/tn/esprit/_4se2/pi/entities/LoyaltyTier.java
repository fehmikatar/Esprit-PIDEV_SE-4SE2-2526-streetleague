package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder
public class LoyaltyTier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "program_id")
    private LoyaltyProgram program;

    private String name;               // ex: "Bronze", "Silver", "Gold"
    private Integer level;             // ordre: 1 = Bronze, 2 = Silver...
    private Integer pointsRequired;    // points minimum pour atteindre ce tier
    private Double extraDiscountRate;  // réduction supplémentaire en % (ex: 5.0)
    private String benefits;           // description textuelle des avantages

    // Optionnel: accès exclusif à certaines catégories de produits
    private String exclusiveAccessTag;
}