package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit._4se2.pi.entities.PostLike;
import tn.esprit._4se2.pi.entities.ReactionType;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    
    // ✅ Vérifier si un utilisateur a déjà réagi à un post
    boolean existsByUserIdAndPostId(Long userId, Long postId);
    
    // ✅ Récupérer la réaction d'un utilisateur sur un post
    Optional<PostLike> findByUserIdAndPostId(Long userId, Long postId);
    
    // ✅ Compter les réactions par type pour un post
    @Query("SELECT pl.reactionType, COUNT(pl) FROM PostLike pl WHERE pl.postId = :postId GROUP BY pl.reactionType")
    List<Object[]> countReactionsByPost(@Param("postId") Long postId);
    
    // ✅ Obtenir toutes les réactions d'un post
    List<PostLike> findByPostId(Long postId);
    
    // ✅ Supprimer toutes les réactions d'un post (pour le nettoyage)
    void deleteByPostId(Long postId);
}