package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import tn.esprit._4se2.pi.entities.PostReaction;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PostReactionRepository extends JpaRepository<PostReaction, Long> {

    Optional<PostReaction> findByPost_IdAndUser_Id(Long postId, Long userId);

    @Query("SELECT r.reactionType, COUNT(r) FROM PostReaction r WHERE r.post.id = :postId GROUP BY r.reactionType")
    List<Object[]> countByTypeForPost(Long postId);

    int countByPost_Id(Long postId);
    List<PostReaction> findByPost_Id(Long postId);
List<PostReaction> findByPost_IdAndReactionType(Long postId, String reactionType);
}