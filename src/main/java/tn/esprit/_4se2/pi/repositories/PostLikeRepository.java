package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
<<<<<<< Updated upstream
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.entities.PostLike;

import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findByPostIdAndUserId(Long postId, Long userId);

    boolean existsByPostIdAndUserId(Long postId, Long userId);
}
=======
import tn.esprit._4se2.pi.entities.Like;

import java.util.List;

public interface PostLikeRepository extends JpaRepository<Like, Long> {

    List<Like> findByUser_Id(Long userId);

    List<Like> findByPost_Id(Long postId);

    boolean existsByUser_IdAndPost_Id(Long userId, Long postId);

    int countByPost_Id(Long postId);

    void deleteByUser_IdAndPost_Id(Long userId, Long postId);
}
>>>>>>> Stashed changes
