package tn.esprit._4se2.pi.restcontrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import tn.esprit._4se2.pi.entities.Like;
import tn.esprit._4se2.pi.entities.Post;
import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.repositories.PostLikeRepository;
import tn.esprit._4se2.pi.repositories.PostRepository;
import tn.esprit._4se2.pi.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/posts/{postId}/like")
@RequiredArgsConstructor
public class PostLikeController {

    private final PostRepository postRepository;
    private final PostLikeRepository postLikeRepository;
    private final UserRepository userRepository;

    /**
     * Toggle like on a post.
     * - If the user already liked it → remove the like.
     * - If not → add a like.
     * Returns the updated like count and liked status.
     */
    @PostMapping
    @Transactional
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = resolveCurrentUser(authentication);
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }

        Long userId = currentUser.getId();
        boolean alreadyLiked = postLikeRepository.existsByUser_IdAndPost_Id(userId, postId);

        if (alreadyLiked) {
            postLikeRepository.deleteByUser_IdAndPost_Id(userId, postId);
        } else {
            Like like = new Like();
            like.setUser(currentUser);
            like.setPost(post);
            like.setCreatedAt(LocalDateTime.now());
            postLikeRepository.save(like);
        }

        int newCount = postLikeRepository.countByPost_Id(postId);
        boolean nowLiked = !alreadyLiked;

        return ResponseEntity.ok(Map.of(
                "liked", nowLiked,
                "likesCount", newCount
        ));
    }

    private User resolveCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            return null;
        }
        return userRepository.findByEmail(authentication.getName()).orElse(null);
    }
}
