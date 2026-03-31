package tn.esprit._4se2.pi.mappers;

import tn.esprit._4se2.pi.dto.TeamRequest;
import tn.esprit._4se2.pi.dto.TeamResponse;
import tn.esprit._4se2.pi.entities.Team;

public class TeamMapper {
    public static Team toEntity(TeamRequest dto) {
        Team team = new Team();
        team.setName(dto.getName());
        return team;
    }

    public static TeamResponse toDto(Team team) {
        TeamResponse dto = new TeamResponse();
        dto.setId(team.getId());
        dto.setName(team.getName());
        return dto;
    }
}
