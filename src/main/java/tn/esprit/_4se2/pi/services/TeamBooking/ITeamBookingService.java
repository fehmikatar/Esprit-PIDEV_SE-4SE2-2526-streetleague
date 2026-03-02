package tn.esprit._4se2.pi.services.TeamBooking;

import tn.esprit._4se2.pi.dto.TeamBookingRequest;
import tn.esprit._4se2.pi.dto.TeamBookingResponse;
import java.util.List;

public interface ITeamBookingService {
    TeamBookingResponse createTeamBooking(TeamBookingRequest request);
    TeamBookingResponse getTeamBookingById(Long id);
    List<TeamBookingResponse> getAllTeamBookings();
    List<TeamBookingResponse> getTeamBookingsByTeamId(Long teamId);
    List<TeamBookingResponse> getTeamBookingsBySportSpaceId(Long sportSpaceId);
    List<TeamBookingResponse> getTeamBookingsByStatus(String status);
    TeamBookingResponse updateTeamBooking(Long id, TeamBookingRequest request);
    void deleteTeamBooking(Long id);
    void confirmTeamBooking(Long id);
}