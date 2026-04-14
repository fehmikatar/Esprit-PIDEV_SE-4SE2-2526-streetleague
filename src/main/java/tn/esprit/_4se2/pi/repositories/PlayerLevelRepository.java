package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit._4se2.pi.entities.Player;
import tn.esprit._4se2.pi.entities.PlayerLevel;

import java.util.Optional;

public interface PlayerLevelRepository extends JpaRepository<PlayerLevel, Long> {
    Optional<PlayerLevel> findByPlayer(Player player);
}