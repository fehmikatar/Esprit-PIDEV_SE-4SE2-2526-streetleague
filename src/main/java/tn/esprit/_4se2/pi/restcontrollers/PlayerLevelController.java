package tn.esprit._4se2.pi.restcontrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.entities.Player;
import tn.esprit._4se2.pi.entities.PlayerLevel;
import tn.esprit._4se2.pi.repositories.PlayerRepository;
import tn.esprit._4se2.pi.services.PlayerLevel.IPlayerLevelService;

@RestController
@RequestMapping("/api/player-levels")
@RequiredArgsConstructor
public class PlayerLevelController {

    private final IPlayerLevelService playerLevelService;
    private final PlayerRepository playerRepository;

    @GetMapping("/player/{playerId}")
    public ResponseEntity<PlayerLevel> getPlayerLevelByPlayerId(@PathVariable Long playerId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));
        return ResponseEntity.ok(playerLevelService.getPlayerLevel(player));
    }
    @PostMapping("/{playerId}/add-xp")
    public ResponseEntity<PlayerLevel> addXpToPlayer(
            @PathVariable Long playerId,
            @RequestParam int xpGained) {

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found with id: " + playerId));

        PlayerLevel updatedLevel = playerLevelService.addXp(player, xpGained);
        return ResponseEntity.ok(updatedLevel);
    }
}