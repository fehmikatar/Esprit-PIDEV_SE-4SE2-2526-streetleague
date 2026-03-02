package tn.esprit._4se2.pi.services.TeamMember;

import tn.esprit._4se2.pi.entities.TeamMember;
import tn.esprit._4se2.pi.entities.TeamMemberId;

import java.util.List;

public interface ITeamMemberService {
    TeamMember addTeamMember(TeamMember teamMember);

    List<TeamMember> getAllTeamMembers();

    TeamMember getTeamMemberById(TeamMemberId id);

    TeamMember updateTeamMember(TeamMember teamMember);

    void deleteTeamMember(TeamMemberId id);
}
