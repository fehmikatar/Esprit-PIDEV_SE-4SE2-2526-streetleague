package tn.esprit._4se2.pi.services.Team;

import tn.esprit._4se2.pi.dto.*;

import java.util.List;

public interface ITeamService {

    TeamResponse createTeam(TeamRequest request, Long authenticatedUserId);

    TeamResponse getTeamById(Long teamId, Long authenticatedUserId);

    List<TeamResponse> getAllTeams(String sport, String city, String level);

    TeamResponse updateTeam(Long teamId, TeamRequest request, Long authenticatedUserId);

    void deleteTeam(Long teamId, Long authenticatedUserId);

    List<TeamMemberResponse> getTeamMembers(Long teamId);

    void requestToJoin(Long teamId, JoinRequestRequest request, Long authenticatedUserId);

    List<JoinRequestResponse> getJoinRequests(Long teamId, Long authenticatedUserId);

    void handleJoinRequest(Long teamId, Long requestId, JoinRequestActionRequest action, Long authenticatedUserId);

    void leaveTeam(Long teamId, Long authenticatedUserId);

    void removeMember(Long teamId, Long targetUserId, Long authenticatedUserId);

    void changeMemberRole(Long teamId, Long targetUserId, ChangeMemberRoleRequest request, Long authenticatedUserId);
}
