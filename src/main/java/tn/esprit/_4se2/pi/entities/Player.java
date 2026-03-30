package tn.esprit._4se2.pi.entities;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Player extends User {
    @ElementCollection
    private List<String> sports = new ArrayList<>();
    private String position;
    @OneToOne(mappedBy = "player")
    private PlayerLevel level;
    @OneToMany(mappedBy = "player")
    private List<Performance> performances;
    @OneToMany(mappedBy = "player")
    private List<BadgePlayer> badgePlayers = new ArrayList<>();
}