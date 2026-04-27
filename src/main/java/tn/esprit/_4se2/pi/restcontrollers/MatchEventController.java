package tn.esprit._4se2.pi.restcontrollers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import tn.esprit._4se2.pi.dto.MatchEvent.PlayerStatsDto;
import tn.esprit._4se2.pi.dto.MatchEvent.TimelineEventDto;
import tn.esprit._4se2.pi.dto.MatchEvent.MatchEventDTOs;
import tn.esprit._4se2.pi.entities.MatchEvent;
import tn.esprit._4se2.pi.services.MatchEvent.MatchEventService;

import java.util.List;

@RestController
@RequestMapping("/api/match-events")
@CrossOrigin(origins = "*")
public class MatchEventController {
    private final MatchEventService matchEventService;

    public MatchEventController(MatchEventService matchEventService) {
        this.matchEventService = matchEventService;
    }

    /**
     * Logs an event — validates that expelled players cannot score
     */
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody MatchEventDTOs.MatchEventRequestDTO request) {
        try {
            MatchEvent event = new MatchEvent();
            event.setMatchId(request.getMatchId());
            event.setEventType(request.getType());
            event.setMinute(request.getMinute());
            event.setTeamId(request.getTeamId());
            event.setPlayerId(request.getPlayerId());
            event.setDescription(request.getDescription());
            event.setPoints(request.getPoints());

            MatchEvent saved = matchEventService.logEvent(event);
            return ResponseEntity.ok(saved);
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
            return ResponseEntity.internalServerError().body("Internal Error: " + ex.getMessage());
        }
    }

    @GetMapping("/match/{matchId}")
    public List<MatchEvent> getByMatchId(@PathVariable Long matchId) {
        return matchEventService.getByMatchId(matchId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        matchEventService.delete(id);
    }

    // ─── MÉTIER 1 — Event Timeline Engine ────────────────────────────────────
    @GetMapping("/match/{matchId}/timeline")
    public ResponseEntity<List<TimelineEventDto>> getTimeline(@PathVariable Long matchId) {
        return ResponseEntity.ok(matchEventService.getTimeline(matchId));
    }

    // ─── MÉTIER 2 — Player Stats Aggregator ──────────────────────────────────
    @GetMapping("/match/{matchId}/player-stats")
    public ResponseEntity<PlayerStatsDto> getPlayerStats(@PathVariable Long matchId) {
        return ResponseEntity.ok(matchEventService.getPlayerStats(matchId));
    }
}
