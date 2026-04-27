package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< Updated upstream
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.entities.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
=======
import tn.esprit._4se2.pi.entities.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost_IdOrderByCreatedAtAsc(Long postId);
    int countByPost_Id(Long postId);
>>>>>>> Stashed changes
}
