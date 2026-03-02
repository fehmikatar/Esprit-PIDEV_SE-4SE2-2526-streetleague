package tn.esprit._4se2.pi.restcontrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.dto.PlayerRequest;
import tn.esprit._4se2.pi.dto.PlayerResponse;
import tn.esprit._4se2.pi.services.Player.IPlayerService;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerRestController {

    private final IPlayerService playerService;

    @PostMapping
    public ResponseEntity<PlayerResponse> createPlayer(@Valid @RequestBody PlayerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(playerService.createPlayer(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerResponse> getPlayerById(@PathVariable Long id) {
        return ResponseEntity.ok(playerService.getPlayerById(id));
    }

    @GetMapping
    public ResponseEntity<List<PlayerResponse>> getAllPlayers() {
        return ResponseEntity.ok(playerService.getAllPlayers());
    }

    @GetMapping("/skill-level/{skillLevel}")
    public ResponseEntity<List<PlayerResponse>> getPlayersBySkillLevel(@PathVariable Integer skillLevel) {
        return ResponseEntity.ok(playerService.getPlayersBySkillLevel(skillLevel));
    }

    @GetMapping("/position/{position}")
    public ResponseEntity<List<PlayerResponse>> getPlayersByPosition(@PathVariable String position) {
        return ResponseEntity.ok(playerService.getPlayersByPosition(position));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlayerResponse> updatePlayer(
            @PathVariable Long id,
            @Valid @RequestBody PlayerRequest request) {
        return ResponseEntity.ok(playerService.updatePlayer(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Long id) {
        playerService.deletePlayer(id);
        return ResponseEntity.noContent().build();
    }
}