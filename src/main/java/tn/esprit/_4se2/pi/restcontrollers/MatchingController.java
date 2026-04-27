package tn.esprit._4se2.pi.restcontrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.dto.MatchingDTOs;
import tn.esprit._4se2.pi.services.MatchingService;

import java.util.List;

@RestController
@RequestMapping("/api/matching")
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingService matchingService;

    @GetMapping("/teams/{playerId}")
    public ResponseEntity<List<MatchingDTOs.MatchResponse>> getBestTeamsForPlayer(
            @PathVariable Long playerId,
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(matchingService.getBestTeamsForPlayer(playerId, limit));
    }

    @GetMapping("/players/{teamId}")
    public ResponseEntity<List<MatchingDTOs.MatchResponse>> getBestPlayersForTeam(
            @PathVariable Long teamId,
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(matchingService.getBestPlayersForTeam(teamId, limit));
    }

    @PostMapping("/recommend/teams")
    public ResponseEntity<List<MatchingDTOs.MatchResponse>> getBestTeamsForProfile(
            @RequestBody MatchingDTOs.PlayerProfileRequest profile,
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(matchingService.getBestTeamsForProfile(profile, limit));
    }
}
