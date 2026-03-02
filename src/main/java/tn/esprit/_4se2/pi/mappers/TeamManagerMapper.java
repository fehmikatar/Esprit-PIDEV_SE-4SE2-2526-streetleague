package tn.esprit._4se2.pi.mappers;

import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.dto.TeamManagerRequest;
import tn.esprit._4se2.pi.dto.TeamManagerResponse;
import tn.esprit._4se2.pi.entities.TeamManager;
import java.time.LocalDateTime;

@Component
public class TeamManagerMapper {

    public TeamManager toEntity(TeamManagerRequest request) {
        if (request == null) return null;

        TeamManager teamManager = new TeamManager();
        teamManager.setFirstName(request.getFirstName());
        teamManager.setLastName(request.getLastName());
        teamManager.setEmail(request.getEmail());
        teamManager.setPhone(request.getPhone());
        teamManager.setPasswordHash(request.getPassword());
        teamManager.setTeamName(request.getTeamName());
        teamManager.setTeamCode(request.getTeamCode());
        teamManager.setExperience(request.getExperience());
        teamManager.setCreatedAt(LocalDateTime.now());
        teamManager.setIsActive(true);
        return teamManager;
    }

    public TeamManagerResponse toResponse(TeamManager entity) {
        if (entity == null) return null;

        return TeamManagerResponse.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .teamName(entity.getTeamName())
                .teamCode(entity.getTeamCode())
                .experience(entity.getExperience())
                .createdAt(entity.getCreatedAt())
                .isActive(entity.getIsActive())
                .build();
    }

    public void updateEntity(TeamManagerRequest request, TeamManager teamManager) {
        if (request == null || teamManager == null) return;

        teamManager.setFirstName(request.getFirstName());
        teamManager.setLastName(request.getLastName());
        teamManager.setEmail(request.getEmail());
        teamManager.setPhone(request.getPhone());
        teamManager.setTeamName(request.getTeamName());
        teamManager.setTeamCode(request.getTeamCode());
        teamManager.setExperience(request.getExperience());
    }
}