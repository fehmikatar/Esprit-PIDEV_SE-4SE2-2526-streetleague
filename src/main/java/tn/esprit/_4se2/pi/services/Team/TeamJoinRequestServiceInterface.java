package tn.esprit._4se2.pi.services.Team;

import tn.esprit._4se2.pi.entities.TeamJoinRequest;
import java.util.List;

public interface TeamJoinRequestServiceInterface {
    TeamJoinRequest createJoinRequest(Long teamId, String userEmail, String message);
    List<TeamJoinRequest> getUserJoinRequests(String userEmail);
    List<TeamJoinRequest> getTeamJoinRequests(Long teamId);
    TeamJoinRequest approveJoinRequest(Long requestId, String reviewerEmail);
    TeamJoinRequest rejectJoinRequest(Long requestId, String reviewerEmail);
    
    // Nouvelle méthode pour l'admin
    List<TeamJoinRequest> getAllPendingJoinRequests();
}