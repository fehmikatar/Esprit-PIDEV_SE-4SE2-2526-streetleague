package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.entities.Player;
import tn.esprit._4se2.pi.entities.enums.SkillLevel;
import tn.esprit._4se2.pi.entities.enums.PlayPosition;
import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    List<Player> findBySkillLevel(SkillLevel skillLevel);
    List<Player> findByPosition(PlayPosition position);
    List<Player> findByRatingGreaterThanEqual(Double rating);
}