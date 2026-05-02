package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.Enum.PostStatus;
import tn.esprit._4se2.pi.entities.CommunityComment;

import java.util.List;

@Repository
public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {

    List<CommunityComment> findByPostIdAndStatusOrderByCreatedAtAsc(Long postId, PostStatus status);
}
