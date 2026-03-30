package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;
import tn.esprit._4se2.pi.entities.Order;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class LoyaltyClient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "program_id")
    private LoyaltyProgram program;
    private String currentTier;
    private int totalPoints;
    private int pointsBalance;
    @OneToMany(mappedBy = "client")
    private List<Order> orders = new ArrayList<>();
}