package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.entities.Booking;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);
    List<Booking> findBySportSpaceId(Long sportSpaceId);
    List<Booking> findByStatus(String status);
    List<Booking> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
}