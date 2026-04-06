package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit._4se2.pi.entities.BadgePlayer;
import tn.esprit._4se2.pi.entities.Player;
import tn.esprit._4se2.pi.entities.Badge;

public interface BadgePlayerRepository extends JpaRepository<BadgePlayer, Long> {
    boolean existsByPlayerAndBadge(Player player, Badge badge);  // anti-doublon
}