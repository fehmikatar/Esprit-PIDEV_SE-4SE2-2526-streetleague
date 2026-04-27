package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< Updated upstream
import org.springframework.stereotype.Repository;
=======
import org.springframework.data.jpa.repository.Query;
>>>>>>> Stashed changes
import tn.esprit._4se2.pi.Enum.JoinRequestStatus;
import tn.esprit._4se2.pi.entities.TeamJoinRequest;

import java.util.List;
import java.util.Optional;

<<<<<<< Updated upstream
@Repository
public interface TeamJoinRequestRepository extends JpaRepository<TeamJoinRequest, Long> {

    List<TeamJoinRequest> findAllByTeamIdAndStatus(Long teamId, JoinRequestStatus status);

    List<TeamJoinRequest> findAllByTeamIdOrderByCreatedAtDesc(Long teamId);

    boolean existsByTeamIdAndUserIdAndStatus(Long teamId, Long userId, JoinRequestStatus status);

    Optional<TeamJoinRequest> findByIdAndTeamId(Long id, Long teamId);
}
=======
public interface TeamJoinRequestRepository extends JpaRepository<TeamJoinRequest, Long> {

    boolean existsByTeam_IdAndUser_IdAndStatus(Long teamId, Long userId, JoinRequestStatus status);

    Optional<TeamJoinRequest> findFirstByTeam_IdAndUser_IdAndStatus(Long teamId, Long userId, JoinRequestStatus status);

    @Query("""
        select r from TeamJoinRequest r
        join fetch r.team t
        join fetch r.user u
        where r.status = :status
        order by r.createdAt desc
        """)
    List<TeamJoinRequest> findAllByStatusWithRelations(JoinRequestStatus status);

    void deleteByTeam_Id(Long teamId);
}
>>>>>>> Stashed changes
