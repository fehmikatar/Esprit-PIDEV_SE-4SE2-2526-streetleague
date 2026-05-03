package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.entities.CommentLike;
import tn.esprit._4se2.pi.entities.ReactionType;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByCommentIdAndUserId(Long commentId, Long userId);
    List<CommentLike> findByCommentId(Long commentId);
    void deleteByCommentIdAndUserId(Long commentId, Long userId);
}
