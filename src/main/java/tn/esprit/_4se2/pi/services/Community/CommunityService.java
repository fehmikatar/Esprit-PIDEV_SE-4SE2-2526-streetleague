package tn.esprit._4se2.pi.services.Community;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit._4se2.pi.dto.Community.CommentResponse;
import tn.esprit._4se2.pi.dto.Community.CreateCommentRequest;
import tn.esprit._4se2.pi.dto.Community.CreatePostRequest;
import tn.esprit._4se2.pi.dto.Community.PostResponse;
import tn.esprit._4se2.pi.dto.Community.ReactionSummary;
import tn.esprit._4se2.pi.dto.Community.UserReactionResponse;
import tn.esprit._4se2.pi.entities.CommentLike;
import tn.esprit._4se2.pi.entities.CommunityPost;
import tn.esprit._4se2.pi.entities.PostComment;
import tn.esprit._4se2.pi.entities.PostLike;
import tn.esprit._4se2.pi.entities.ReactionType;
import tn.esprit._4se2.pi.entities.Team;
import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.repositories.CommentLikeRepository;
import tn.esprit._4se2.pi.repositories.CommunityPostRepository;
import tn.esprit._4se2.pi.repositories.PostCommentRepository;
import tn.esprit._4se2.pi.repositories.PostLikeRepository;
import tn.esprit._4se2.pi.repositories.TeamMemberRepository;
import tn.esprit._4se2.pi.repositories.TeamRepository;
import tn.esprit._4se2.pi.repositories.UserRepository;
import tn.esprit._4se2.pi.services.N8nIntegrationService;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private static final int PAGE_SIZE = 20;
    private static final String UPLOAD_BASE = "uploads/community/";
    private static final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif");
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5 MB

    private final CommunityPostRepository postRepository;
    private final PostCommentRepository commentRepository;
    private final PostLikeRepository likeRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;
    private final BadWordsFilterService badWordsFilter;
    private final CommentLikeRepository commentLikeRepository;
    private final N8nIntegrationService n8nService;

    // ── Helper: resolve current authenticated user ────────────────────────────

    private User currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));
    }

    private void requireTeamMember(Long teamId, Long userId) {
        if (!teamMemberRepository.existsByTeamIdAndUserId(teamId, userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "You must be a member of this team to access its community");
        }
    }

    // ── Mapping helpers ───────────────────────────────────────────────────────

    private PostResponse.AuthorInfo toAuthorInfo(User user) {
        PostResponse.AuthorInfo info = new PostResponse.AuthorInfo();
        info.setId(user.getId());
        info.setFirstName(user.getFirstName());
        info.setLastName(user.getLastName());
        info.setProfileImageUrl(user.getProfileImageUrl());
        return info;
    }

    private PostResponse toPostResponse(CommunityPost post, Long currentUserId) {
        PostResponse r = new PostResponse();
        r.setId(post.getId());
        r.setTitle("Post");
        r.setContent(post.getContent());
        r.setImageUrl(post.getImageUrl());
        r.setTeamId(post.getTeam().getId());
        r.setCreatedAt(post.getCreatedAt());
        r.setLikeCount(post.getLikesCount());
        r.setCommentCount(post.getCommentCount());
        r.setLikedByCurrentUser(likeRepository.existsByUserIdAndPostId(currentUserId, post.getId()));
        r.setAuthor(toAuthorInfo(post.getAuthor()));
        return r;
    }

    private CommentResponse toCommentResponse(PostComment comment, Long currentUserId) {
        CommentResponse r = new CommentResponse();
        r.setId(comment.getId());
        r.setContent(comment.getContent());
        r.setPostId(comment.getPost().getId());
        r.setParentId(comment.getParent() != null ? comment.getParent().getId() : null);
        r.setCreatedAt(comment.getCreatedAt());
        r.setAuthor(toAuthorInfo(comment.getAuthor()));

        List<CommentLike> likes = commentLikeRepository.findByCommentId(comment.getId());
        r.setLikeCount(likes.size());
        r.setLikedByCurrentUser(likes.stream().anyMatch(l -> l.getUserId().equals(currentUserId)));
        r.setCurrentUserReaction(likes.stream()
                .filter(l -> l.getUserId().equals(currentUserId))
                .map(CommentLike::getReactionType)
                .findFirst()
                .orElse(null));

        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            r.setReplies(comment.getReplies().stream()
                    .map(reply -> this.toCommentResponse(reply, currentUserId))
                    .collect(Collectors.toList()));
        } else {
            r.setReplies(new java.util.ArrayList<>());
        }

        return r;
    }

    // ── Posts ─────────────────────────────────────────────────────────────────

    public Page<PostResponse> getTeamPosts(Long teamId, int page) {
        User user = currentUser();
        requireTeamMember(teamId, user.getId());
        return postRepository
                .findByTeamIdOrderByCreatedAtDesc(teamId, PageRequest.of(page, PAGE_SIZE))
                .map(post -> toPostResponse(post, user.getId()));
    }

    @Transactional
    public PostResponse createPost(Long teamId, CreatePostRequest request) {
        User user = currentUser();
        requireTeamMember(teamId, user.getId());

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found: " + teamId));

        CommunityPost post = new CommunityPost();
        post.setContent(badWordsFilter.filterText(request.getContent()));
        post.setAuthor(user);
        post.setTeam(team);

        return toPostResponse(postRepository.save(post), user.getId());
    }

    @Transactional
    public PostResponse uploadPostImage(Long postId, MultipartFile file) throws IOException {
        User user = currentUser();
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found: " + postId));

        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new RuntimeException("Only the post author can upload an image");
        }

        validateImageFile(file);

        String ext = getExtension(file.getOriginalFilename());
        String dir = UPLOAD_BASE + "team_" + post.getTeam().getId() + "/";
        Files.createDirectories(Paths.get(dir));

        String filename = UUID.randomUUID() + "." + ext;
        Files.copy(file.getInputStream(), Paths.get(dir + filename), StandardCopyOption.REPLACE_EXISTING);

        post.setImageUrl("/" + dir + filename);
        return toPostResponse(postRepository.save(post), user.getId());
    }

    @Transactional
    public void deletePost(Long postId) {
        User user = currentUser();
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found: " + postId));

        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new RuntimeException("Only the post author can delete this post");
        }

        postRepository.delete(post);
    }

    // ── Comments ──────────────────────────────────────────────────────────────

    public List<CommentResponse> getPostComments(Long postId) {
        User user = currentUser();
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found: " + postId));
        requireTeamMember(post.getTeam().getId(), user.getId());

        return commentRepository.findByPostIdAndParentIsNullOrderByCreatedAtAsc(postId)
                .stream()
                .map(comment -> this.toCommentResponse(comment, user.getId()))
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentResponse addComment(Long postId, CreateCommentRequest request) {
        User user = currentUser();
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found: " + postId));
        requireTeamMember(post.getTeam().getId(), user.getId());

        PostComment comment = new PostComment();
        comment.setContent(badWordsFilter.filterText(request.getContent()));
        comment.setAuthor(user);
        comment.setPost(post);

        if (request.getParentId() != null) {
            PostComment parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("Parent comment not found: " + request.getParentId()));
            
            if (!parent.getPost().getId().equals(postId)) {
                throw new RuntimeException("Parent comment does not belong to this post");
            }
            
            comment.setParent(parent);
        }

        PostComment saved = commentRepository.save(comment);

        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);

        return toCommentResponse(saved, user.getId());
    }

    @Transactional
    public void deleteComment(Long commentId) {
        User user = currentUser();
        PostComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found: " + commentId));

        if (!comment.getAuthor().getId().equals(user.getId())) {
            throw new RuntimeException("Only the comment author can delete this comment");
        }

        CommunityPost post = comment.getPost();
        commentRepository.delete(comment);
        post.setCommentCount(Math.max(0, post.getCommentCount() - 1));
        postRepository.save(post);
    }

    // ── Likes & Reactions ─────────────────────────────────────────────────────

    @Transactional
    public PostResponse likePost(Long postId) {
        User user = currentUser();
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found: " + postId));
        requireTeamMember(post.getTeam().getId(), user.getId());

        if (!likeRepository.existsByUserIdAndPostId(user.getId(), postId)) {
            PostLike like = new PostLike();
            like.setUser(user);
            like.setPost(post);
            likeRepository.save(like);
            post.setLikesCount(post.getLikesCount() + 1);
            postRepository.save(post);
        }

        return toPostResponse(post, user.getId());
    }

    @Transactional
    public PostResponse unlikePost(Long postId) {
        User user = currentUser();
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found: " + postId));
        requireTeamMember(post.getTeam().getId(), user.getId());

        Optional<PostLike> like = likeRepository.findByUserIdAndPostId(user.getId(), postId);
        if (like.isPresent()) {
            likeRepository.delete(like.get());
            post.setLikesCount(Math.max(0, post.getLikesCount() - 1));
            postRepository.save(post);
        }

        return toPostResponse(post, user.getId());
    }

    public List<PostResponse.AuthorInfo> getPostLikers(Long postId) {
        User user = currentUser();
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found: " + postId));
        requireTeamMember(post.getTeam().getId(), user.getId());

        return likeRepository.findByPostId(postId).stream()
                .map(like -> {
                    User likerUser = like.getUser();
                    if (likerUser == null) {
                        throw new RuntimeException("User not found");
                    }
                    return toAuthorInfo(likerUser);
                })
                .collect(Collectors.toList());
    }

    /**
     * Ajoute ou change une réaction sur un post
     */
    @Transactional
    public void addOrUpdateReaction(Long postId, String userEmail, ReactionType reactionType) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        if (!teamMemberRepository.existsByTeamIdAndUserId(post.getTeam().getId(), user.getId())) {
            throw new RuntimeException("You must be a team member to react");
        }

        Optional<PostLike> existing = likeRepository.findByUserIdAndPostId(user.getId(), postId);

        if (existing.isPresent()) {
            PostLike like = existing.get();
            like.setReactionType(reactionType);
            likeRepository.save(like);
        } else {
            PostLike newReaction = new PostLike();
            newReaction.setUser(user);
            newReaction.setPost(post);
            newReaction.setReactionType(reactionType);
            likeRepository.save(newReaction);
            
            post.setLikesCount(post.getLikesCount() + 1);
            postRepository.save(post);
        }
    }

    /**
     * Supprimer une réaction sur un post
     */
    @Transactional
    public void removeReaction(Long postId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Optional<PostLike> like = likeRepository.findByUserIdAndPostId(user.getId(), postId);
        if (like.isPresent()) {
            likeRepository.delete(like.get());
            
            CommunityPost post = postRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("Post not found"));
            post.setLikesCount(Math.max(0, post.getLikesCount() - 1));
            postRepository.save(post);
        }
    }

    /**
     * Obtenir le résumé des réactions d'un post
     */
    public List<ReactionSummary> getReactionSummary(Long postId) {
        List<Object[]> results = likeRepository.countReactionsByPost(postId);
        
        return results.stream()
                .map(row -> new ReactionSummary(
                    (ReactionType) row[0],
                    ((ReactionType) row[0]).getEmoji(),
                    (Long) row[1]
                ))
                .collect(Collectors.toList());
    }

    /**
     * Obtenir la réaction de l'utilisateur courant sur un post
     */
    public ReactionType getCurrentUserReaction(Long postId, Long userId) {
        return likeRepository.findByUserIdAndPostId(userId, postId)
                .map(PostLike::getReactionType)
                .orElse(null);
    }

    /**
     * Ajoute ou change une réaction sur un commentaire
     */
    @Transactional
    public void addOrUpdateCommentReaction(Long commentId, String userEmail, ReactionType reactionType) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        PostComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        requireTeamMember(comment.getPost().getTeam().getId(), user.getId());
        
        Optional<CommentLike> existing = commentLikeRepository.findByCommentIdAndUserId(commentId, user.getId());
        
        if (existing.isPresent()) {
            CommentLike reaction = existing.get();
            reaction.setReactionType(reactionType);
            commentLikeRepository.save(reaction);
        } else {
            CommentLike newReaction = CommentLike.builder()
                    .userId(user.getId())
                    .commentId(commentId)
                    .reactionType(reactionType)
                    .build();
            commentLikeRepository.save(newReaction);
        }
    }

    /**
     * Supprime une réaction sur un commentaire
     */
    @Transactional
    public void removeCommentReaction(Long commentId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        commentLikeRepository.deleteByCommentIdAndUserId(commentId, user.getId());
    }

    /**
     * Obtenir la liste des utilisateurs ayant réagi à un post
     */
    public List<UserReactionResponse> getPostReactionUsers(Long postId) {
        return likeRepository.findByPostId(postId).stream()
                .map(like -> {
                    User reactionUser = like.getUser();
                    if (reactionUser == null) {
                        throw new RuntimeException("User not found for reaction");
                    }
                    return UserReactionResponse.builder()
                            .userId(reactionUser.getId())
                            .firstName(reactionUser.getFirstName())
                            .lastName(reactionUser.getLastName())
                            .profileImageUrl(reactionUser.getProfileImageUrl())
                            .reactionType(like.getReactionType())
                            .build();
                })
                .collect(Collectors.toList());
    }

    // ── AI Suggestion ─────────────────────────────────────────────────────────

    public String getAiPostSuggestion(Long teamId) {
        User user;
        try {
            user = currentUser();
        } catch (Exception e) {
            user = new User();
            user.setId(0L);
            user.setFirstName("System");
            user.setLastName("AI");
            user.setEmail("ai@streetleague.com");
            user.setRole(tn.esprit._4se2.pi.Enum.Role.ROLE_ADMIN);
        }
        
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found"));
                
        return n8nService.getPostSuggestion(teamId, team.getName(), team.getSport(), user);
    }

    // ── File helpers ──────────────────────────────────────────────────────────

    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) throw new RuntimeException("File is empty");
        if (file.getSize() > MAX_FILE_SIZE) throw new RuntimeException("File exceeds 5 MB limit");
        String ext = getExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new RuntimeException("Unsupported file type. Allowed: jpg, jpeg, png, gif");
        }
    }

    private String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "jpg";
        return filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
    }
}