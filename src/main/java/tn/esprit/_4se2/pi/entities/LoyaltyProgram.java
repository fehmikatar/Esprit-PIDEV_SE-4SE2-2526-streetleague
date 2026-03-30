package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class LoyaltyProgram {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int pointsPerPurchase;
    @ElementCollection
    private List<String> tiers = new ArrayList<>(); // e.g., "Bronze", "Silver", "Gold"
    @OneToMany(mappedBy = "program")
    private List<LoyaltyClient> clients = new ArrayList<>();
}