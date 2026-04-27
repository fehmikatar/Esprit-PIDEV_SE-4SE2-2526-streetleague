package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< Updated upstream
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.entities.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}
=======
import tn.esprit._4se2.pi.entities.Post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByCommunity_IdOrderByCreatedAtDesc(Long communityId);

    List<Post> findByCommunity_IdAndCreatedAtAfter(Long communityId, LocalDateTime createdAt);

    Optional<Post> findByIdAndCommunity_Id(Long id, Long communityId);
}
>>>>>>> Stashed changes
