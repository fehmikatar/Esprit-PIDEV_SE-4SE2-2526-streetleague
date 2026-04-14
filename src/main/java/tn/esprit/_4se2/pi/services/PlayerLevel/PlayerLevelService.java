package tn.esprit._4se2.pi.services.PlayerLevel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.entities.Player;
import tn.esprit._4se2.pi.entities.PlayerLevel;
import tn.esprit._4se2.pi.repositories.PlayerLevelRepository;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PlayerLevelService implements IPlayerLevelService {

    private final PlayerLevelRepository playerLevelRepository;

    private static final int BASE_XP = 100;
    private static final double MULTIPLIER = 1.5;

    @Override
    public PlayerLevel addXp(Player player, int xpGained) {
        log.info("Adding {} XP to player {}", xpGained, player.getId());

        PlayerLevel playerLevel = getOrCreatePlayerLevel(player);
        int newTotalXp = playerLevel.getTotalXp() + xpGained;
        playerLevel.setTotalXp(newTotalXp);
        int newLevel = getLevelForXp(newTotalXp);
        playerLevel.setCurrentLevel(newLevel);

        log.info("Player {} reached level {} with {} total XP", player.getId(), newLevel, newTotalXp);
        return playerLevelRepository.save(playerLevel);
    }

    @Override
    // Plus de readOnly = false (par défaut), car on peut créer un PlayerLevel
    public PlayerLevel getPlayerLevel(Player player) {
        log.info("Fetching level for player {}", player.getId());
        return getOrCreatePlayerLevel(player);
    }

    @Override
    public int getLevelForXp(int totalXp) {
        if (totalXp <= 0) return 1;

        int level = 1;
        int cumulativeXp = 0;

        while (true) {
            // XP nécessaire pour passer du niveau actuel au suivant
            int xpForNextLevel = (int) (BASE_XP * Math.pow(MULTIPLIER, level - 1));
            cumulativeXp += xpForNextLevel;

            if (totalXp < cumulativeXp) {
                break;
            }
            level++;
        }
        return level;
    }

    private PlayerLevel getOrCreatePlayerLevel(Player player) {
        return playerLevelRepository.findByPlayer(player)
                .orElseGet(() -> {
                    PlayerLevel newLevel = new PlayerLevel();
                    newLevel.setPlayer(player);
                    newLevel.setCurrentLevel(1);
                    newLevel.setTotalXp(0);
                    log.info("Created new PlayerLevel for player {}", player.getId());
                    return playerLevelRepository.save(newLevel);
                });
    }
}