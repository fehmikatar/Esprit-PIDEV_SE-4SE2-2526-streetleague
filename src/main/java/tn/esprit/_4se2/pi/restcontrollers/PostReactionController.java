package tn.esprit._4se2.pi.restcontrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import tn.esprit._4se2.pi.entities.Post;
import tn.esprit._4se2.pi.entities.PostReaction;
import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.repositories.PostReactionRepository;
import tn.esprit._4se2.pi.repositories.PostRepository;
import tn.esprit._4se2.pi.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/posts/{postId}/react")
@RequiredArgsConstructor
public class PostReactionController {

    private final PostRepository postRepository;
    private final PostReactionRepository reactionRepository;
    private final UserRepository userRepository;

    private static final Set<String> VALID_TYPES = Set.of(
        "LIKE", "LOVE", "HAHA", "WOW", "SAD", "ANGRY"
    );

    @PostMapping
    public ResponseEntity<Map<String, Object>> react(
            @PathVariable Long postId,
            @RequestBody Map<String, String> body) {

        String reactionType = (body.getOrDefault("type", "LIKE")).toUpperCase();
        if (!VALID_TYPES.contains(reactionType)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid reaction type");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        Optional<PostReaction> existing = reactionRepository.findByPost_IdAndUser_Id(postId, currentUser.getId());

        String action;
        String finalType = null;

        if (existing.isPresent()) {
            if (existing.get().getReactionType().equals(reactionType)) {
                // Même réaction → supprimer (toggle off)
                reactionRepository.delete(existing.get());
                action = "REMOVED";
            } else {
                // Changer de réaction
                existing.get().setReactionType(reactionType);
                existing.get().setCreatedAt(LocalDateTime.now());
                reactionRepository.save(existing.get());
                action = "CHANGED";
                finalType = reactionType;
            }
        } else {
            // Nouvelle réaction
            PostReaction reaction = PostReaction.builder()
                    .post(post)
                    .user(currentUser)
                    .reactionType(reactionType)
                    .createdAt(LocalDateTime.now())
                    .build();
            reactionRepository.save(reaction);
            action = "ADDED";
            finalType = reactionType;
        }

        // Compter toutes les réactions par type
        Map<String, Long> counts = new LinkedHashMap<>();
        reactionRepository.countByTypeForPost(postId).forEach(row ->
            counts.put((String) row[0], (Long) row[1])
        );

        int total = reactionRepository.countByPost_Id(postId);

        Map<String, Object> response = new HashMap<>();
        response.put("action", action);
        response.put("reactionType", finalType);
        response.put("totalCount", total);
        response.put("counts", counts);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getReactions(
            @PathVariable Long postId) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = auth != null ? userRepository.findByEmail(auth.getName()).orElse(null) : null;

        Map<String, Long> counts = new LinkedHashMap<>();
        reactionRepository.countByTypeForPost(postId).forEach(row ->
            counts.put((String) row[0], (Long) row[1])
        );

        int total = reactionRepository.countByPost_Id(postId);
        String myReaction = null;
        if (currentUser != null) {
            myReaction = reactionRepository
                .findByPost_IdAndUser_Id(postId, currentUser.getId())
                .map(PostReaction::getReactionType)
                .orElse(null);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("totalCount", total);
        response.put("counts", counts);
        response.put("myReaction", myReaction);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users")
public ResponseEntity<List<Map<String, Object>>> getReactionUsers(
        @PathVariable Long postId,
        @RequestParam(required = false) String type) {

    List<PostReaction> reactions;
    if (type != null && !type.isBlank()) {
        reactions = reactionRepository.findByPost_IdAndReactionType(postId, type.toUpperCase());
    } else {
        reactions = reactionRepository.findByPost_Id(postId);
    }

    List<Map<String, Object>> result = reactions.stream().map(r -> {
        Map<String, Object> item = new HashMap<>();
        item.put("userId", r.getUser().getId());
        item.put("firstName", r.getUser().getFirstName());
        item.put("lastName", r.getUser().getLastName());
        item.put("reactionType", r.getReactionType());
        return item;
    }).collect(Collectors.toList());

    return ResponseEntity.ok(result);
}
}