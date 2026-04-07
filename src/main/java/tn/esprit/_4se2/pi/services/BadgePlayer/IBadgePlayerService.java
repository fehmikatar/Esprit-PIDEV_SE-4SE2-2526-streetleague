package tn.esprit._4se2.pi.services.BadgePlayer;

import tn.esprit._4se2.pi.entities.Badge;
import tn.esprit._4se2.pi.entities.Performance;
import tn.esprit._4se2.pi.entities.Player;
import tn.esprit._4se2.pi.entities.BadgePlayer;

public interface IBadgePlayerService {
    BadgePlayer awardBadgeToPlayer(Player player, Badge badge, Performance performance);
}