package tn.esprit._4se2.pi.restcontrollers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import tn.esprit._4se2.pi.dto.Community.AddReactionRequest;
import tn.esprit._4se2.pi.dto.Community.CommentResponse;
import tn.esprit._4se2.pi.dto.Community.CreateCommentRequest;
import tn.esprit._4se2.pi.dto.Community.CreatePostRequest;
import tn.esprit._4se2.pi.dto.Community.PostResponse;
import tn.esprit._4se2.pi.dto.Community.ReactionSummary;
import tn.esprit._4se2.pi.dto.Community.UserReactionResponse;
import tn.esprit._4se2.pi.services.Community.CommunityService;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    // ── Posts ─────────────────────────────────────────────────────────────────

    @GetMapping("/team/{teamId}/posts")
    public ResponseEntity<Page<PostResponse>> getTeamPosts(
            @PathVariable Long teamId,
            @RequestParam(defaultValue = "0") int page) {
        return ResponseEntity.ok(communityService.getTeamPosts(teamId, page));
    }

    @PostMapping("/team/{teamId}/posts")
    public ResponseEntity<PostResponse> createPost(
            @PathVariable Long teamId,
            @Valid @RequestBody CreatePostRequest request) {
        return ResponseEntity.ok(communityService.createPost(teamId, request));
    }

    @PostMapping("/posts/{postId}/upload-image")
    public ResponseEntity<PostResponse> uploadImage(
            @PathVariable Long postId,
            @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(communityService.uploadPostImage(postId, file));
    }

    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId) {
        communityService.deletePost(postId);
        return ResponseEntity.noContent().build();
    }

    // ── Comments ──────────────────────────────────────────────────────────────

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long postId) {
        return ResponseEntity.ok(communityService.getPostComments(postId));
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long postId,
            @Valid @RequestBody CreateCommentRequest request) {
        return ResponseEntity.ok(communityService.addComment(postId, request));
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        communityService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    // ── Likes (ancienne version) ──────────────────────────────────────────────

    @PostMapping("/posts/{postId}/like")
    public ResponseEntity<PostResponse> likePost(@PathVariable Long postId) {
        return ResponseEntity.ok(communityService.likePost(postId));
    }

    @DeleteMapping("/posts/{postId}/like")
    public ResponseEntity<PostResponse> unlikePost(@PathVariable Long postId) {
        return ResponseEntity.ok(communityService.unlikePost(postId));
    }

    @GetMapping("/posts/{postId}/likes")
    public ResponseEntity<List<PostResponse.AuthorInfo>> getPostLikers(@PathVariable Long postId) {
        return ResponseEntity.ok(communityService.getPostLikers(postId));
    }

    // ── Reactions (nouvelle version avec emojis) ──────────────────────────────

    /**
     * Ajouter ou changer une réaction
     */
    @PostMapping("/posts/{postId}/react")
    public ResponseEntity<Void> addReaction(
            @PathVariable Long postId,
            @RequestBody AddReactionRequest request,
            Principal principal) {
        
        communityService.addOrUpdateReaction(postId, principal.getName(), request.getReactionType());
        return ResponseEntity.ok().build();
    }

    /**
     * Supprimer une réaction
     */
    @DeleteMapping("/posts/{postId}/react")
    public ResponseEntity<Void> removeReaction(
            @PathVariable Long postId,
            Principal principal) {
        
        communityService.removeReaction(postId, principal.getName());
        return ResponseEntity.ok().build();
    }

    /**
     * Obtenir le résumé des réactions
     */
    @GetMapping("/posts/{postId}/reactions")
    public ResponseEntity<List<ReactionSummary>> getReactions(@PathVariable Long postId) {
        return ResponseEntity.ok(communityService.getReactionSummary(postId));
    }

    // ── Comment Reactions ──────────────────────────────────────────────────

    @PostMapping("/comments/{commentId}/react")
    public ResponseEntity<Void> addCommentReaction(
            @PathVariable Long commentId,
            @RequestBody AddReactionRequest request,
            Principal principal) {
        communityService.addOrUpdateCommentReaction(commentId, principal.getName(), request.getReactionType());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/comments/{commentId}/react")
    public ResponseEntity<Void> removeCommentReaction(
            @PathVariable Long commentId,
            Principal principal) {
        communityService.removeCommentReaction(commentId, principal.getName());
        return ResponseEntity.ok().build();
    }

    /**
     * Obtenir la liste des utilisateurs qui ont réagi à un post
     */
    @GetMapping("/posts/{postId}/reaction-users")
    public ResponseEntity<List<UserReactionResponse>> getPostReactionUsers(@PathVariable Long postId) {
        return ResponseEntity.ok(communityService.getPostReactionUsers(postId));
    }
}