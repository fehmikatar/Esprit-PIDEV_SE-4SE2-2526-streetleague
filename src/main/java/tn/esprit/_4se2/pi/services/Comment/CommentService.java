package tn.esprit._4se2.pi.services.Comment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.entities.Comment;
import tn.esprit._4se2.pi.entities.Post;
import tn.esprit._4se2.pi.repositories.CommentRepository;
import tn.esprit._4se2.pi.repositories.PostRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository; // Nécessaire pour charger le Post associé

    @Override
    public Comment addComment(Comment comment) {
        // Initialiser la date de création
        comment.setCreatedAt(LocalDateTime.now());

        // Vérifier et charger le Post associé
        if (comment.getPost() != null && comment.getPost().getId() != null) {
            Post post = postRepository.findById(comment.getPost().getId())
                    .orElseThrow(() -> new RuntimeException("Post non trouvé avec l'id : " + comment.getPost().getId()));
            comment.setPost(post);
        } else {
            // Un commentaire doit être associé à un post (selon votre logique métier)
            throw new RuntimeException("Le commentaire doit être associé à un post");
        }

        return commentRepository.save(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getAllComments() {
        return commentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Comment getCommentById(Long id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commentaire non trouvé avec l'id : " + id));
    }

    @Override
    @Transactional
    public Comment updateComment(Comment comment) {
        // Récupérer le commentaire existant
        Comment existingComment = getCommentById(comment.getId());

        // Mettre à jour le contenu (seul champ modifiable)
        if (comment.getContent() != null) {
            existingComment.setContent(comment.getContent());
        }

        // Mettre à jour le post associé si un nouvel ID est fourni
        if (comment.getPost() != null && comment.getPost().getId() != null) {
            Post post = postRepository.findById(comment.getPost().getId())
                    .orElseThrow(() -> new RuntimeException("Post non trouvé avec l'id : " + comment.getPost().getId()));
            existingComment.setPost(post);
        }
        // Si comment.getPost() est null, on garde l'ancien post (pas de changement)

        // La transaction garantit que les modifications sont persistées
        return existingComment;
    }

    @Override
    public void deleteComment(Long id) {
        commentRepository.deleteById(id);
    }
}