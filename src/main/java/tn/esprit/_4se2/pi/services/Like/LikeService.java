package tn.esprit._4se2.pi.services.Like;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.entities.*;
import tn.esprit._4se2.pi.repositories.*;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LikeService implements ILikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public Like addLike(Like like) {
        // 1. Initialiser la date
        like.setCreatedAt(LocalDateTime.now());

        // 2. Récupérer et associer l'utilisateur (obligatoire)
        if (like.getUser() == null || like.getUser().getId() == null) {
            throw new RuntimeException("L'utilisateur est requis");
        }
        User user = userRepository.findById(like.getUser().getId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id : " + like.getUser().getId()));
        like.setUser(user);

        // 3. Vérifier qu'une seule cible (post ou comment) est fournie
        boolean hasPost = like.getPost() != null && like.getPost().getId() != null;
        boolean hasComment = like.getComment() != null && like.getComment().getId() != null;

        if (!hasPost && !hasComment) {
            throw new RuntimeException("Le like doit être associé à un post ou à un commentaire");
        }
        if (hasPost && hasComment) {
            throw new RuntimeException("Le like ne peut pas être associé à la fois à un post et à un commentaire");
        }

        // 4. Traiter le cas Post
        if (hasPost) {
            Post post = postRepository.findById(like.getPost().getId())
                    .orElseThrow(() -> new RuntimeException("Post non trouvé avec l'id : " + like.getPost().getId()));
            like.setPost(post);
            like.setComment(null); // Au cas où

            // Vérifier si l'utilisateur a déjà liké ce post
            if (likeRepository.existsByUserAndPost(user, post)) {
                throw new RuntimeException("Vous avez déjà liké ce post");
            }
        }

        // 5. Traiter le cas Comment
        if (hasComment) {
            Comment comment = commentRepository.findById(like.getComment().getId())
                    .orElseThrow(() -> new RuntimeException("Commentaire non trouvé avec l'id : " + like.getComment().getId()));
            like.setComment(comment);
            like.setPost(null); // Au cas où

            // Vérifier si l'utilisateur a déjà liké ce commentaire
            if (likeRepository.existsByUserAndComment(user, comment)) {
                throw new RuntimeException("Vous avez déjà liké ce commentaire");
            }
        }

        // 6. Sauvegarder
        return likeRepository.save(like);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Like> getAllLikes() {
        return likeRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Like getLikeById(Long id) {
        return likeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Like non trouvé avec l'id : " + id));
    }

    @Override
    @Transactional
    public void deleteLike(Long id) {
        Like like = getLikeById(id);
        likeRepository.delete(like);
    }
}