package tn.esprit._4se2.pi.services.Community;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.Enum.*;
import tn.esprit._4se2.pi.dto.*;
import tn.esprit._4se2.pi.entities.*;
import tn.esprit._4se2.pi.exception.*;
import tn.esprit._4se2.pi.repositories.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityService implements ICommunityService {

    private final CommunityPostRepository postRepository;
    private final CommunityCommentRepository commentRepository;
    private final PostLikeRepository likeRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;

    // ──────────────────────────────────────────────
    // POSTS
    // ──────────────────────────────────────────────

    @Override
    @Transactional
    public CommunityPostResponse createPost(CommunityPostRequest request, Long userId) {
        User author = findUserOrThrow(userId);

        Team team = null;
        if (request.getPostType() == PostType.TEAM) {
            if (request.getTeamId() == null) {
                throw new IllegalArgumentException("teamId is required for TEAM posts.");
            }
            team = teamRepository.findById(request.getTeamId())
                    .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + request.getTeamId()));

            // Verify user is a member of the team
            if (!teamMemberRepository.existsByTeamIdAndUserIdAndStatus(
                    request.getTeamId(), userId, MemberStatus.ACTIVE)) {
                throw new ForbiddenException("You must be a member of the team to post in it.");
            }
        }

        CommunityPost post = CommunityPost.builder()
                .author(author)
                .content(request.getContent())
                .postType(request.getPostType())
                .team(team)
                .imageUrl(request.getImageUrl())
                .status(PostStatus.VISIBLE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .likesCount(0)
                .build();

        post = postRepository.save(post);
        return mapToPostResponse(post, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CommunityPostResponse> getGlobalPosts(int page, int size, Long authenticatedUserId) {
        Pageable pageable = PageRequest.of(page, size);
        return postRepository
                .findByPostTypeNotAndStatusOrderByCreatedAtDesc(PostType.TEAM, PostStatus.VISIBLE, pageable)
                .map(p -> mapToPostResponse(p, authenticatedUserId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommunityPostResponse> getTeamPosts(Long teamId, Long authenticatedUserId) {
        teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));

        if (!teamMemberRepository.existsByTeamIdAndUserIdAndStatus(teamId, authenticatedUserId, MemberStatus.ACTIVE)) {
            throw new ForbiddenException("You must be a member of the team to view team posts.");
        }

        return postRepository.findByTeamIdAndStatusOrderByCreatedAtDesc(teamId, PostStatus.VISIBLE)
                .stream()
                .map(p -> mapToPostResponse(p, authenticatedUserId))
                .collect(Collectors.toList());
    }

    // ──────────────────────────────────────────────
    // COMMENTS
    // ──────────────────────────────────────────────

    @Override
    @Transactional
    public CommunityCommentResponse addComment(Long postId, CommunityCommentRequest request, Long userId) {
        CommunityPost post = findPostOrThrow(postId);
        User author = findUserOrThrow(userId);

        CommunityComment comment = CommunityComment.builder()
                .post(post)
                .author(author)
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .status(PostStatus.VISIBLE)
                .build();

        comment = commentRepository.save(comment);
        return mapToCommentResponse(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommunityCommentResponse> getComments(Long postId) {
        findPostOrThrow(postId);
        return commentRepository
                .findByPostIdAndStatusOrderByCreatedAtAsc(postId, PostStatus.VISIBLE)
                .stream()
                .map(this::mapToCommentResponse)
                .collect(Collectors.toList());
    }

    // ──────────────────────────────────────────────
    // LIKES
    // ──────────────────────────────────────────────

    @Override
    @Transactional
    public void toggleLike(Long postId, Long userId) {
        CommunityPost post = findPostOrThrow(postId);
        User user = findUserOrThrow(userId);

        likeRepository.findByPostIdAndUserId(postId, userId).ifPresentOrElse(
                existingLike -> {
                    likeRepository.delete(existingLike);
                    post.setLikesCount(Math.max(0, post.getLikesCount() - 1));
                    postRepository.save(post);
                },
                () -> {
                    PostLike newLike = PostLike.builder()
                            .post(post)
                            .user(user)
                            .createdAt(LocalDateTime.now())
                            .build();
                    likeRepository.save(newLike);
                    post.setLikesCount(post.getLikesCount() + 1);
                    postRepository.save(post);
                }
        );
    }

    // ──────────────────────────────────────────────
    // DELETE
    // ──────────────────────────────────────────────

    @Override
    @Transactional
    public void deletePost(Long postId, Long userId) {
        CommunityPost post = findPostOrThrow(postId);
        User user = findUserOrThrow(userId);

        boolean isAuthor = post.getAuthor().getId().equals(userId);
        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;

        if (!isAuthor && !isAdmin) {
            throw new ForbiddenException("You do not have permission to delete this post.");
        }

        post.setStatus(PostStatus.DELETED);
        postRepository.save(post);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        CommunityComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));
        User user = findUserOrThrow(userId);

        boolean isAuthor = comment.getAuthor().getId().equals(userId);
        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;

        if (!isAuthor && !isAdmin) {
            throw new ForbiddenException("You do not have permission to delete this comment.");
        }

        comment.setStatus(PostStatus.DELETED);
        commentRepository.save(comment);
    }

    // ──────────────────────────────────────────────
    // HELPERS / MAPPING
    // ──────────────────────────────────────────────

    private CommunityPost findPostOrThrow(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    private CommunityPostResponse mapToPostResponse(CommunityPost post, Long authenticatedUserId) {
        boolean isLiked = authenticatedUserId != null &&
                likeRepository.existsByPostIdAndUserId(post.getId(), authenticatedUserId);

        User author = post.getAuthor();
        Team team = post.getTeam();

        return CommunityPostResponse.builder()
                .id(post.getId())
                .authorId(author.getId())
                .authorFirstName(author.getFirstName())
                .authorLastName(author.getLastName())
                .content(post.getContent())
                .postType(post.getPostType())
                .status(post.getStatus())
                .teamId(team != null ? team.getId() : null)
                .teamName(team != null ? team.getName() : null)
                .imageUrl(post.getImageUrl())
                .likesCount(post.getLikesCount())
                .commentsCount(post.getComments().size())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .isLikedByCurrentUser(isLiked)
                .build();
    }

    private CommunityCommentResponse mapToCommentResponse(CommunityComment comment) {
        User author = comment.getAuthor();
        return CommunityCommentResponse.builder()
                .id(comment.getId())
                .authorId(author.getId())
                .authorFirstName(author.getFirstName())
                .authorLastName(author.getLastName())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
