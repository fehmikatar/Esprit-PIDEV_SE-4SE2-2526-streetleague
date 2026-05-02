package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.Enum.JoinRequestStatus;
import tn.esprit._4se2.pi.entities.Team;
import tn.esprit._4se2.pi.entities.TeamJoinRequest;
import tn.esprit._4se2.pi.entities.User;

import java.util.List;

@Repository
public interface TeamJoinRequestRepository extends JpaRepository<TeamJoinRequest, Long> {

    List<TeamJoinRequest> findByUser(User user);

    List<TeamJoinRequest> findByTeamAndStatus(Team team, JoinRequestStatus status);

    boolean existsByTeamAndUserAndStatus(Team team, User user, JoinRequestStatus status);
    
    // Nouvelle méthode pour récupérer toutes les demandes par statut
    List<TeamJoinRequest> findByStatus(JoinRequestStatus status);
}