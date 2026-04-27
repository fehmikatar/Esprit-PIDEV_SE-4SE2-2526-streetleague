package tn.esprit._4se2.pi.restcontrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit._4se2.pi.services.TeamJoinRequest.TeamJoinRequestService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/team-members")
@RequiredArgsConstructor
public class TeamMemberRequestController {

    private final TeamJoinRequestService teamJoinRequestService;

    // Compatibility alias for existing frontend; official endpoint is /api/teams/join-requests
    @GetMapping("/requests")
    public List<Map<String, Object>> getPendingJoinRequestsForAdmin() {
        return teamJoinRequestService.getPendingRequestsForAdmin();
    }
}