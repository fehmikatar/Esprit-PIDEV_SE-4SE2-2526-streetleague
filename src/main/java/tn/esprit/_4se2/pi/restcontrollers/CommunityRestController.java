package tn.esprit._4se2.pi.restcontrollers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.dto.*;
import tn.esprit._4se2.pi.repositories.UserRepository;
import tn.esprit._4se2.pi.services.Community.ICommunityService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CommunityRestController {

    private final ICommunityService communityService;
    private final UserRepository userRepository;

    // ──────────────────────────────────────────────
    // POSTS
    // ──────────────────────────────────────────────

    @PostMapping("/api/community/posts")
    public ResponseEntity<CommunityPostResponse> createPost(
            @Valid @RequestBody CommunityPostRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = resolveUserId(userDetails);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(communityService.createPost(request, userId));
    }

    @GetMapping("/api/community/posts")
    public ResponseEntity<Page<CommunityPostResponse>> getGlobalPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = resolveUserId(userDetails);
        return ResponseEntity.ok(communityService.getGlobalPosts(page, size, userId));
    }

    // ──────────────────────────────────────────────
    // TEAM POSTS — nested under /api/teams
    // ──────────────────────────────────────────────

    @GetMapping("/api/teams/{teamId}/posts")
    public ResponseEntity<List<CommunityPostResponse>> getTeamPosts(
            @PathVariable Long teamId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = resolveUserId(userDetails);
        return ResponseEntity.ok(communityService.getTeamPosts(teamId, userId));
    }

    // ──────────────────────────────────────────────
    // COMMENTS
    // ──────────────────────────────────────────────

    @PostMapping("/api/posts/{postId}/comments")
    public ResponseEntity<CommunityCommentResponse> addComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommunityCommentRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = resolveUserId(userDetails);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(communityService.addComment(postId, request, userId));
    }

    @GetMapping("/api/posts/{postId}/comments")
    public ResponseEntity<List<CommunityCommentResponse>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(communityService.getComments(postId));
    }

    // ──────────────────────────────────────────────
    // LIKES
    // ──────────────────────────────────────────────

    @PostMapping("/api/posts/{postId}/like")
    public ResponseEntity<Void> toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = resolveUserId(userDetails);
        communityService.toggleLike(postId, userId);
        return ResponseEntity.ok().build();
    }

    // ──────────────────────────────────────────────
    // DELETE
    // ──────────────────────────────────────────────

    @DeleteMapping("/api/posts/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = resolveUserId(userDetails);
        communityService.deletePost(postId, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = resolveUserId(userDetails);
        communityService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }

    // ──────────────────────────────────────────────
    // HELPER
    // ──────────────────────────────────────────────

    private Long resolveUserId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"))
                .getId();
    }
}
