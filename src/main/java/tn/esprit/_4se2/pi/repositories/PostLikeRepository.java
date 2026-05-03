package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tn.esprit._4se2.pi.entities.PostLike;
import tn.esprit._4se2.pi.entities.ReactionType;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    
    // ✅ Vérifier si un utilisateur a déjà réagi à un post
    @Query("SELECT COUNT(pl) > 0 FROM PostLike pl WHERE pl.user.id = :userId AND pl.post.id = :postId")
    boolean existsByUserIdAndPostId(@Param("userId") Long userId, @Param("postId") Long postId);
    
    // ✅ Récupérer la réaction d'un utilisateur sur un post
    @Query("SELECT pl FROM PostLike pl WHERE pl.user.id = :userId AND pl.post.id = :postId")
    Optional<PostLike> findByUserIdAndPostId(@Param("userId") Long userId, @Param("postId") Long postId);
    
    // ✅ Compter les réactions par type pour un post
    @Query("SELECT pl.reactionType, COUNT(pl) FROM PostLike pl WHERE pl.post.id = :postId GROUP BY pl.reactionType")
    List<Object[]> countReactionsByPost(@Param("postId") Long postId);
    
    // ✅ Obtenir toutes les réactions d'un post
    @Query("SELECT pl FROM PostLike pl WHERE pl.post.id = :postId")
    List<PostLike> findByPostId(@Param("postId") Long postId);
    
    // ✅ Supprimer toutes les réactions d'un post (pour le nettoyage)
    @Query("DELETE FROM PostLike pl WHERE pl.post.id = :postId")
    void deleteByPostId(@Param("postId") Long postId);

    // Methods from integration
    Optional<PostLike> findByPostIdAndUserId(Long postId, Long userId);

    boolean existsByPostIdAndUserId(Long postId, Long userId);
}
