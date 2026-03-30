package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class PlayerLevel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "player_id")
    private Player player;
    private int currentLevel;
    private int totalXp;
}