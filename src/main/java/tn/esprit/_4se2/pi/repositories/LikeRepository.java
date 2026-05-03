package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.entities.Comment;
import tn.esprit._4se2.pi.entities.Like;
import tn.esprit._4se2.pi.entities.Post;
import tn.esprit._4se2.pi.entities.User;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByUserAndPost(User user, Post post);

}
