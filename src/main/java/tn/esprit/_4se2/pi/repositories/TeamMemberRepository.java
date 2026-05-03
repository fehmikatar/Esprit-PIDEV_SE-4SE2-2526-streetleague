package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.Enum.MemberStatus;
import tn.esprit._4se2.pi.Enum.TeamRole;
import tn.esprit._4se2.pi.entities.Team;
import tn.esprit._4se2.pi.entities.TeamMember;
import tn.esprit._4se2.pi.entities.TeamMemberId;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, TeamMemberId> {

    @Query("SELECT tm FROM TeamMember tm WHERE tm.id.teamId = :teamId")
    List<TeamMember> findByTeamId(@Param("teamId") Long teamId);

    @Query("SELECT tm FROM TeamMember tm WHERE tm.id.userId = :userId")
    List<TeamMember> findByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(tm) > 0 FROM TeamMember tm WHERE tm.id.teamId = :teamId AND tm.id.userId = :userId")
    boolean existsByTeamIdAndUserId(@Param("teamId") Long teamId, @Param("userId") Long userId);

    // Methods from integration
    Optional<TeamMember> findByTeamIdAndUserId(Long teamId, Long userId);

    List<TeamMember> findAllByTeamId(Long teamId);

    List<TeamMember> findAllByUserId(Long userId);

    boolean existsByUserIdAndTeamSport(Long userId, String sport);

    Optional<TeamMember> findByTeamIdAndUserIdAndRole(Long teamId, Long userId, TeamMember.Role role);

    Optional<TeamMember> findByTeamAndRole(Team team, TeamMember.Role role);

    long countByTeamId(Long teamId);

    default Optional<TeamMember> findByTeamIdAndUserIdAndStatus(Long teamId, Long userId, MemberStatus status) {
        if (status != MemberStatus.ACTIVE) {
            return Optional.empty();
        }
        return findByTeamIdAndUserId(teamId, userId);
    }

    default List<TeamMember> findAllByTeamIdAndStatus(Long teamId, MemberStatus status) {
        if (status != MemberStatus.ACTIVE) {
            return Collections.emptyList();
        }
        return findAllByTeamId(teamId);
    }

    default List<TeamMember> findAllByUserIdAndStatus(Long userId, MemberStatus status) {
        if (status != MemberStatus.ACTIVE) {
            return Collections.emptyList();
        }
        return findAllByUserId(userId);
    }

    default boolean existsByTeamIdAndUserIdAndStatus(Long teamId, Long userId, MemberStatus status) {
        return status == MemberStatus.ACTIVE && existsByTeamIdAndUserId(teamId, userId);
    }

    default boolean existsByUserIdAndStatusAndTeamSport(Long userId, MemberStatus status, String sport) {
        return status == MemberStatus.ACTIVE && existsByUserIdAndTeamSport(userId, sport);
    }

    default Optional<TeamMember> findByTeamIdAndUserIdAndTeamRole(Long teamId, Long userId, TeamRole teamRole) {
        return findByTeamIdAndUserIdAndRole(teamId, userId, mapTeamRole(teamRole));
    }

    default Optional<TeamMember> findByTeamAndTeamRole(Team team, TeamRole teamRole) {
        return findByTeamAndRole(team, mapTeamRole(teamRole));
    }

    default long countByTeamIdAndStatus(Long teamId, MemberStatus status) {
        return status == MemberStatus.ACTIVE ? countByTeamId(teamId) : 0L;
    }

    private static TeamMember.Role mapTeamRole(TeamRole teamRole) {
        if (teamRole == TeamRole.CAPTAIN || teamRole == TeamRole.CO_CAPTAIN) {
            return TeamMember.Role.RESPONSIBLE;
        }
        return TeamMember.Role.MEMBER;
    }
}
