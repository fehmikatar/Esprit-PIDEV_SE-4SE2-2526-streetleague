package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "badge_player")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class BadgePlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "badge_id")
    private Badge badge;

    private LocalDate obtainDate;
}