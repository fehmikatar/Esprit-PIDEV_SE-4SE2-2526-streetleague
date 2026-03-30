package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.Enum.MemberStatus;
import tn.esprit._4se2.pi.Enum.TeamRole;
import tn.esprit._4se2.pi.entities.Team;
import tn.esprit._4se2.pi.entities.TeamMember;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    Optional<TeamMember> findByTeamIdAndUserIdAndStatus(Long teamId, Long userId, MemberStatus status);

    List<TeamMember> findAllByTeamIdAndStatus(Long teamId, MemberStatus status);

    boolean existsByTeamIdAndUserIdAndStatus(Long teamId, Long userId, MemberStatus status);

    Optional<TeamMember> findByTeamIdAndUserIdAndTeamRole(Long teamId, Long userId, TeamRole teamRole);

    Optional<TeamMember> findByTeamAndTeamRole(Team team, TeamRole teamRole);

    long countByTeamIdAndStatus(Long teamId, MemberStatus status);
}
