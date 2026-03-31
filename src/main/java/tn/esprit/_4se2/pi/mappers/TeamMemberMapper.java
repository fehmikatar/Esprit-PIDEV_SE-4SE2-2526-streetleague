package tn.esprit._4se2.pi.mappers;

import tn.esprit._4se2.pi.Enum.MemberStatus;
import tn.esprit._4se2.pi.Enum.TeamRole;
import tn.esprit._4se2.pi.dto.TeamMember.TeamMemberRequest;
import tn.esprit._4se2.pi.dto.TeamMember.TeamMemberResponse;
import tn.esprit._4se2.pi.entities.Team;
import tn.esprit._4se2.pi.entities.TeamMember;
import tn.esprit._4se2.pi.entities.User;

import java.time.LocalDateTime;

public class TeamMemberMapper {

    public static TeamMember toEntity(
            TeamMemberRequest dto,
            User user,
            Team team) {
        TeamMember teamMember = new TeamMember();
        teamMember.setUser(user);
        teamMember.setTeam(team);
        teamMember.setJoinedAt(LocalDateTime.now());
        teamMember.setStatus(MemberStatus.ACTIVE);
        if (dto.getRole() != null) {
            teamMember.setTeamRole(TeamRole.valueOf(dto.getRole()));
        }
        return teamMember;
    }

    public static TeamMemberResponse toDto(TeamMember teamMember) {
        TeamMemberResponse dto = new TeamMemberResponse();
        dto.setMemberId(teamMember.getId());
        dto.setUserId(teamMember.getUser() != null ? teamMember.getUser().getId() : null);
        dto.setTeamRole(teamMember.getTeamRole());
        dto.setJoinedAt(teamMember.getJoinedAt());
        dto.setStatus(teamMember.getStatus());
        if (teamMember.getUser() != null) {
            dto.setFirstName(teamMember.getUser().getFirstName());
            dto.setLastName(teamMember.getUser().getLastName());
            dto.setEmail(teamMember.getUser().getEmail());
        }
        return dto;
    }
}
