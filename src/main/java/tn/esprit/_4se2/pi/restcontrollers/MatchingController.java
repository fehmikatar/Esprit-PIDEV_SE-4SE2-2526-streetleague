package tn.esprit._4se2.pi.restcontrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.dto.Matching.MatchingDTOs.*;
import tn.esprit._4se2.pi.services.Matching.MatchingService;

import java.util.List;

@RestController
@RequestMapping("/api/matching")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class MatchingController {

    private final MatchingService matchingService;

    @PostMapping("/recommend/teams")
    public ResponseEntity<List<MatchResponse>> getBestTeamsForProfile(
            @RequestBody PlayerProfileMatchingRequest request,
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(matchingService.getBestTeamsForProfile(request, limit));
    }

    @GetMapping("/teams/{playerId}")
    public ResponseEntity<List<MatchResponse>> getBestTeamsForPlayer(
            @PathVariable Long playerId,
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(matchingService.getBestTeamsForPlayer(playerId, limit));
    }

    @GetMapping("/players/{teamId}")
    public ResponseEntity<List<MatchResponse>> getBestPlayersForTeam(
            @PathVariable Long teamId,
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(matchingService.getBestPlayersForTeam(teamId, limit));
    }
}
