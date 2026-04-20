package tn.esprit._4se2.pi.services.PlayerLevel;

import tn.esprit._4se2.pi.Enum.Role;
import tn.esprit._4se2.pi.dto.PlayerRanking.PlayerRankingDTO;
import tn.esprit._4se2.pi.dto.PositionStats.PositionStatsDTO;
import tn.esprit._4se2.pi.entities.Player;
import tn.esprit._4se2.pi.entities.PlayerLevel;

import java.util.List;

public interface IPlayerLevelService {
    PlayerLevel addXp(Player player, int xpGained);
    PlayerLevel getPlayerLevel(Player player);   // méthode à ajouter
    int getLevelForXp(int totalXp);
    List<PlayerRankingDTO> getPlayerRankingWithStats();

    List<PositionStatsDTO> getPositionStatsWithMinPlayers(int minPlayers, double minAvgLevel);
}