package tn.esprit._4se2.pi.services.Community;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit._4se2.pi.dto.Community.CommentResponse;
import tn.esprit._4se2.pi.dto.Community.CreateCommentRequest;
import tn.esprit._4se2.pi.dto.Community.CreatePostRequest;
import tn.esprit._4se2.pi.dto.Community.PostResponse;
import tn.esprit._4se2.pi.dto.Community.ReactionSummary;
import tn.esprit._4se2.pi.entities.*;
// ✅ Si manquant
import tn.esprit._4se2.pi.repositories.*;
import tn.esprit._4se2.pi.dto.Community.ReactionSummary;

import java.util.stream.Collectors;
import java.util.Optional;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
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
        private final CommunityPostRepository communityPostRepository;  // ✅ Doit être présent
    private final PostLikeRepository postLikeRepository;

    

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
        r.setContent(post.getContent());
        r.setImageUrl(post.getImageUrl());
        r.setTeamId(post.getTeam().getId());
        r.setCreatedAt(post.getCreatedAt());
        r.setLikeCount(post.getLikeCount());
        r.setCommentCount(post.getCommentCount());
        r.setLikedByCurrentUser(likeRepository.existsByUserIdAndPostId(currentUserId, post.getId()));
        r.setAuthor(toAuthorInfo(post.getAuthor()));
        return r;
    }

    private CommentResponse toCommentResponse(PostComment comment) {
        CommentResponse r = new CommentResponse();
        r.setId(comment.getId());
        r.setContent(comment.getContent());
        r.setPostId(comment.getPost().getId());
        r.setCreatedAt(comment.getCreatedAt());
        r.setAuthor(toAuthorInfo(comment.getAuthor()));
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

        return commentRepository.findByPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(this::toCommentResponse)
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
        PostComment saved = commentRepository.save(comment);

        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);

        return toCommentResponse(saved);
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

    // ── Likes ─────────────────────────────────────────────────────────────────

@Transactional
public PostResponse likePost(Long postId) {
    User user = currentUser();
    CommunityPost post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found: " + postId));
    requireTeamMember(post.getTeam().getId(), user.getId());

    if (!likeRepository.existsByUserIdAndPostId(user.getId(), postId)) {
        PostLike like = new PostLike();
        like.setUserId(user.getId());  // ✅ Changé
        like.setPostId(postId);        // ✅ Changé
        likeRepository.save(like);
        post.setLikeCount(post.getLikeCount() + 1);
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
        post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
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
                // ✅ Récupérer l'utilisateur via son ID
                User likerUser = userRepository.findById(like.getUserId())
                        .orElseThrow(() -> new RuntimeException("User not found"));
                return toAuthorInfo(likerUser);
            })
            .collect(Collectors.toList());
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

// Ajoutez ces méthodes dans CommunityService

/**
 * Ajoute ou change une réaction sur un post
 */
public void addOrUpdateReaction(Long postId, String userEmail, ReactionType reactionType) {
    // Récupérer l'utilisateur
    User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
    
    // Vérifier que l'utilisateur est membre de l'équipe
    CommunityPost post = postRepository.findById(postId)
            .orElseThrow(() -> new RuntimeException("Post not found"));
    
    if (!teamMemberRepository.existsByTeamIdAndUserId(post.getTeam().getId(), user.getId())) {  // ✅ Changé
        throw new RuntimeException("You must be a team member to react");
    }
    
    // Chercher si une réaction existe déjà
    Optional<PostLike> existingReaction = likeRepository.findByUserIdAndPostId(user.getId(), postId);
    
    if (existingReaction.isPresent()) {
        // Modifier la réaction existante
        PostLike reaction = existingReaction.get();
        reaction.setReactionType(reactionType);
        likeRepository.save(reaction);
    } else {
        // Créer une nouvelle réaction
        PostLike newReaction = new PostLike();
        newReaction.setUserId(user.getId());   // ✅ Changé
        newReaction.setPostId(postId);         // ✅ Changé
        newReaction.setReactionType(reactionType);
        likeRepository.save(newReaction);
        
        // Incrémenter le compteur
        post.setLikeCount(post.getLikeCount() + 1);
        postRepository.save(post);
    }
}
/**
 * Supprime une réaction
 */
public void removeReaction(Long postId, String userEmail) {
    User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
    
    Optional<PostLike> reaction = likeRepository.findByUserIdAndPostId(user.getId(), postId);
    
    if (reaction.isPresent()) {
        likeRepository.delete(reaction.get());
        
        // Décrémenter le compteur
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
        postRepository.save(post);
    }
}

/**
 * Obtenir le résumé des réactions
 */
public List<ReactionSummary> getReactionSummary(Long postId) {
    List<Object[]> results = postLikeRepository.countReactionsByPost(postId);
    
    return results.stream()
            .map(row -> new ReactionSummary(
                    (ReactionType) row[0],
                    ((ReactionType) row[0]).getEmoji(),
                    (Long) row[1]
            ))
            .collect(Collectors.toList());
}

/**
 * Obtenir la réaction de l'utilisateur courant
 */
public ReactionType getCurrentUserReaction(Long postId, Long userId) {
    return postLikeRepository.findByUserIdAndPostId(userId, postId)
            .map(PostLike::getReactionType)
            .orElse(null);
}
    
}
