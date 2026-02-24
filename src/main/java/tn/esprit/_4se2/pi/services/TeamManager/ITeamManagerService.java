package tn.esprit._4se2.pi.services.TeamManager;

import tn.esprit._4se2.pi.dto.TeamManagerRequest;
import tn.esprit._4se2.pi.dto.TeamManagerResponse;
import java.util.List;

public interface ITeamManagerService {
    TeamManagerResponse createTeamManager(TeamManagerRequest request);
    TeamManagerResponse getTeamManagerById(Long id);
    List<TeamManagerResponse> getAllTeamManagers();
    TeamManagerResponse getTeamManagerByTeamCode(String teamCode);
    TeamManagerResponse updateTeamManager(Long id, TeamManagerRequest request);
    void deleteTeamManager(Long id);
}