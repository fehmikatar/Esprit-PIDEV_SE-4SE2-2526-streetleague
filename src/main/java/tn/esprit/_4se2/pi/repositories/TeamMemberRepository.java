package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit._4se2.pi.entities.TeamMember;
import tn.esprit._4se2.pi.entities.TeamMemberId;

import java.util.List;

public interface TeamMemberRepository extends JpaRepository<TeamMember, TeamMemberId> {

    @Query("SELECT tm FROM TeamMember tm WHERE tm.id.teamId = :teamId")
    List<TeamMember> findByTeamId(@Param("teamId") Long teamId);

    @Query("SELECT tm FROM TeamMember tm WHERE tm.id.userId = :userId")
    List<TeamMember> findByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(tm) > 0 FROM TeamMember tm WHERE tm.id.teamId = :teamId AND tm.id.userId = :userId")
    boolean existsByTeamIdAndUserId(@Param("teamId") Long teamId, @Param("userId") Long userId);
}
