package tn.esprit._4se2.pi.restcontrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.entities.Badge;
import tn.esprit._4se2.pi.entities.BadgePlayer;
import tn.esprit._4se2.pi.entities.Performance;
import tn.esprit._4se2.pi.entities.Player;
import tn.esprit._4se2.pi.repositories.BadgeRepository;
import tn.esprit._4se2.pi.repositories.PerformanceRepository;
import tn.esprit._4se2.pi.repositories.PlayerRepository;
import tn.esprit._4se2.pi.services.BadgePlayer.IBadgePlayerService;

import java.util.Map;

@RestController
@RequestMapping("/api/badge-player")
@RequiredArgsConstructor
public class BadgePlayerController {

    private final IBadgePlayerService badgePlayerService;
    private final PlayerRepository playerRepository;
    private final BadgeRepository badgeRepository;
    private final PerformanceRepository performanceRepository;

    @PostMapping("/award")
    public ResponseEntity<BadgePlayer> awardBadgeToPlayer(@RequestBody Map<String, Long> request) {
        Long playerId = request.get("playerId");
        Long badgeId = request.get("badgeId");
        Long performanceId = request.get("performanceId");

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new RuntimeException("Player not found"));
        Badge badge = badgeRepository.findById(badgeId)
                .orElseThrow(() -> new RuntimeException("Badge not found"));
        Performance performance = performanceRepository.findById(performanceId)
                .orElse(null); // Optional

        BadgePlayer awarded = badgePlayerService.awardBadgeToPlayer(player, badge, performance);
        if (awarded == null) {
            return ResponseEntity.badRequest().build(); // Already exists
        }

        return ResponseEntity.ok(awarded);
    }
}
