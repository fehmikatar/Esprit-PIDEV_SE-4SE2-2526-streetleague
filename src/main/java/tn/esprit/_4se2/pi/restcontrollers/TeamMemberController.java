package tn.esprit._4se2.pi.restcontrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.entities.TeamJoinRequest;
import tn.esprit._4se2.pi.services.Team.TeamJoinRequestService;

import java.util.List;

@RestController
@RequestMapping("/api/team-members")
@RequiredArgsConstructor
public class TeamMemberController {

    private final TeamJoinRequestService joinRequestService;

    /**
     * ADMIN voit TOUTES les demandes d'adhésion de TOUTES les équipes
     */
    @GetMapping("/requests")
    public ResponseEntity<List<TeamJoinRequest>> getTeamMemberRequests(
            @AuthenticationPrincipal UserDetails userDetails) {

        try {
            String userEmail = userDetails.getUsername();
            
            // Récupérer TOUTES les demandes PENDING (pour l'admin)
            List<TeamJoinRequest> requests = joinRequestService.getAllPendingJoinRequests();
            
            System.out.println("📊 Nombre total de demandes PENDING: " + requests.size());
            
            return ResponseEntity.ok(requests);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}