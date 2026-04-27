package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< Updated upstream
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

    List<TeamMember> findAllByUserIdAndStatus(Long userId, MemberStatus status);

    boolean existsByTeamIdAndUserIdAndStatus(Long teamId, Long userId, MemberStatus status);

    boolean existsByUserIdAndStatusAndTeamSport(Long userId, MemberStatus status, String sport);

    Optional<TeamMember> findByTeamIdAndUserIdAndTeamRole(Long teamId, Long userId, TeamRole teamRole);

    Optional<TeamMember> findByTeamAndTeamRole(Team team, TeamRole teamRole);

    long countByTeamIdAndStatus(Long teamId, MemberStatus status);
}
=======
import tn.esprit._4se2.pi.entities.TeamMember;

import java.util.List;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    boolean existsByTeam_IdAndUser_Id(Long teamId, Long userId);

    List<TeamMember> findByUser_Id(Long userId);

    void deleteByTeam_Id(Long teamId);
}
>>>>>>> Stashed changes
