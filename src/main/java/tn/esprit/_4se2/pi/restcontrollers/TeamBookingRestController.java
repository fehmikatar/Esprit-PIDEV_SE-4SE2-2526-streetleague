package tn.esprit._4se2.pi.restcontrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.dto.TeamBookingRequest;
import tn.esprit._4se2.pi.dto.TeamBookingResponse;
import tn.esprit._4se2.pi.services.TeamBooking.ITeamBookingService;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/team-bookings")
@RequiredArgsConstructor
public class TeamBookingRestController {

    private final ITeamBookingService teamBookingService;

    @PostMapping
    public ResponseEntity<TeamBookingResponse> createTeamBooking(@Valid @RequestBody TeamBookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(teamBookingService.createTeamBooking(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamBookingResponse> getTeamBookingById(@PathVariable Long id) {
        return ResponseEntity.ok(teamBookingService.getTeamBookingById(id));
    }

    @GetMapping
    public ResponseEntity<List<TeamBookingResponse>> getAllTeamBookings() {
        return ResponseEntity.ok(teamBookingService.getAllTeamBookings());
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<TeamBookingResponse>> getTeamBookingsByTeamId(@PathVariable Long teamId) {
        return ResponseEntity.ok(teamBookingService.getTeamBookingsByTeamId(teamId));
    }

    @GetMapping("/sport-space/{sportSpaceId}")
    public ResponseEntity<List<TeamBookingResponse>> getTeamBookingsBySportSpaceId(@PathVariable Long sportSpaceId) {
        return ResponseEntity.ok(teamBookingService.getTeamBookingsBySportSpaceId(sportSpaceId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<TeamBookingResponse>> getTeamBookingsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(teamBookingService.getTeamBookingsByStatus(status));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamBookingResponse> updateTeamBooking(
            @PathVariable Long id,
            @Valid @RequestBody TeamBookingRequest request) {
        return ResponseEntity.ok(teamBookingService.updateTeamBooking(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeamBooking(@PathVariable Long id) {
        teamBookingService.deleteTeamBooking(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/confirm")
    public ResponseEntity<Void> confirmTeamBooking(@PathVariable Long id) {
        teamBookingService.confirmTeamBooking(id);
        return ResponseEntity.noContent().build();
    }
}