package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.entities.Feedback;
import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findBySportSpaceId(Long sportSpaceId);
    List<Feedback> findBySportSpaceIdAndStatus(Long sportSpaceId, String status);
    List<Feedback> findByUserId(Long userId);
    List<Feedback> findByStatus(String status);
    List<Feedback> findByRatingGreaterThanEqual(Integer rating);
    Optional<Feedback> findByBookingId(Long bookingId);
}
