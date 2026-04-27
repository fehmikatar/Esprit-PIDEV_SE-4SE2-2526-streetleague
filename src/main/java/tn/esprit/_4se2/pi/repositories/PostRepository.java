package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.entities.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
}
