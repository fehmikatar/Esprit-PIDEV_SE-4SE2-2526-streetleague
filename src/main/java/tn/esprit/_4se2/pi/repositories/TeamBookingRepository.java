package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.entities.TeamBooking;
import java.util.List;

@Repository
public interface TeamBookingRepository extends JpaRepository<TeamBooking, Long> {
    List<TeamBooking> findByTeamId(Long teamId);
    List<TeamBooking> findBySportSpaceId(Long sportSpaceId);
    List<TeamBooking> findByStatus(String status);
}