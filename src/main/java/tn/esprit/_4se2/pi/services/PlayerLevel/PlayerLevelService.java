package tn.esprit._4se2.pi.services.PlayerLevel;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.Enum.Role;
import tn.esprit._4se2.pi.dto.PlayerRanking.PlayerRankingDTO;
import tn.esprit._4se2.pi.dto.PositionStats.PositionStatsDTO;
import tn.esprit._4se2.pi.entities.Player;
import tn.esprit._4se2.pi.entities.PlayerLevel;
import tn.esprit._4se2.pi.repositories.PlayerLevelRepository;
import tn.esprit._4se2.pi.repositories.PlayerRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PlayerLevelService implements IPlayerLevelService {

    private final PlayerLevelRepository playerLevelRepository;
    private final PlayerRepository playerRepository;

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
    @Scheduled(cron = "0 0 9 * * *") // tous les jours à 9h
    public void giveDailyBonusToAllPlayers() {
        List<Player> players = playerRepository.findAll();
        players.forEach(player -> addXp(player, 10));
        log.info("Daily 10 XP bonus given to all players");
    }
    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public List<PlayerRankingDTO> getPlayerRankingWithStats() {
        String jpql = """
        SELECT new tn.esprit._4se2.pi.dto.PlayerRankingDTO(
            u.id,
            u.firstName,
            u.lastName,
            u.email,
            pl.currentLevel,
            pl.totalXp,
            p.gamesPlayed,
            CASE WHEN p.gamesPlayed > 0 THEN pl.totalXp / p.gamesPlayed ELSE 0 END
        )
        FROM User u
        JOIN Player p ON u.id = p.id
        LEFT JOIN PlayerLevel pl ON p.id = pl.player.id
        WHERE u.role = :role
        ORDER BY pl.currentLevel DESC, pl.totalXp DESC
        """;

        return entityManager.createQuery(jpql, PlayerRankingDTO.class)
                .setParameter("role", Role.ROLE_PLAYER)
                .getResultList();
    }
    @Override
    public List<PositionStatsDTO> getPositionStatsWithMinPlayers(int minPlayers, double minAvgLevel) {
        String jpql = """
        SELECT new tn.esprit._4se2.pi.dto.PositionStatsDTO(
            p.position,
            COUNT(p.id),
            AVG(pl.currentLevel),
            AVG(pl.totalXp),
            AVG(CASE WHEN p.gamesPlayed > 0 THEN pl.totalXp / p.gamesPlayed ELSE 0 END),
            100.0 * SUM(CASE WHEN p.skillLevel >= 8 AND pl.currentLevel >= 5 THEN 1 ELSE 0 END) / COUNT(p.id)
        )
        FROM Player p
        JOIN p.user u          
        LEFT JOIN PlayerLevel pl ON p.id = pl.player.id
        WHERE u.isActive = true
          AND p.gamesPlayed > 0
        GROUP BY p.position
        HAVING COUNT(p.id) >= :minPlayers AND AVG(pl.currentLevel) >= :minAvgLevel
        ORDER BY AVG(pl.totalXp) DESC
        """;

        return entityManager.createQuery(jpql, PositionStatsDTO.class)
                .setParameter("minPlayers", minPlayers)
                .setParameter("minAvgLevel", minAvgLevel)
                .getResultList();
    }
}