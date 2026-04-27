package tn.esprit._4se2.pi.restcontrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit._4se2.pi.entities.Comment;
import tn.esprit._4se2.pi.entities.Post;
import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.repositories.CommentRepository;
import tn.esprit._4se2.pi.repositories.PostRepository;
import tn.esprit._4se2.pi.repositories.UserRepository;
import tn.esprit._4se2.pi.services.Community.BadWordsFilterService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
public class PostCommentController {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BadWordsFilterService badWordsFilter; // ✅ ajout

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getComments(@PathVariable Long postId) {
        postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        List<Map<String, Object>> result = commentRepository
                .findByPost_IdOrderByCreatedAtAsc(postId)
                .stream()
                .map(this::toMap)
                .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> addComment(
            @PathVariable Long postId,
            @RequestBody Map<String, String> body) {

        String rawContent = body.getOrDefault("content", "").trim();
        if (rawContent.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Comment content must not be blank");
        }

        // ✅ Filtrer les bad words avant sauvegarde
        String content = badWordsFilter.filter(rawContent);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = resolveCurrentUser(authentication);
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setAuthor(currentUser);
        comment.setPost(post);

        Comment saved = commentRepository.save(comment);
        return ResponseEntity.status(HttpStatus.CREATED).body(toMap(saved));
    }

    private Map<String, Object> toMap(Comment comment) {
        String authorFirstName = comment.getAuthor() != null ? comment.getAuthor().getFirstName() : null;
        String authorLastName  = comment.getAuthor() != null ? comment.getAuthor().getLastName()  : null;
        String authorName = (authorFirstName != null ? authorFirstName : "") +
                (authorLastName != null ? " " + authorLastName : "");
        return Map.of(
                "id",              comment.getId(),
                "content",         comment.getContent() != null ? comment.getContent() : "",
                "createdAt",       comment.getCreatedAt() != null ? comment.getCreatedAt().toString() : "",
                "authorId",        comment.getAuthor() != null ? comment.getAuthor().getId() : 0L,
                "authorFirstName", authorFirstName != null ? authorFirstName : "",
                "authorLastName",  authorLastName  != null ? authorLastName  : "",
                "authorName",      authorName.trim()
        );
    }

    private User resolveCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            return null;
        }
        return userRepository.findByEmail(authentication.getName()).orElse(null);
    }
}