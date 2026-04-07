package tn.esprit._4se2.pi.services.PlayerLevel;

import tn.esprit._4se2.pi.entities.Player;
import tn.esprit._4se2.pi.entities.PlayerLevel;

public interface IPlayerLevelService {
    PlayerLevel addXp(Player player, int xpGained);
    PlayerLevel getPlayerLevel(Player player);   // méthode à ajouter
    int getLevelForXp(int totalXp);
}