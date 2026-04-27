package tn.esprit._4se2.pi.services.Community;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit._4se2.pi.Enum.Role;
import tn.esprit._4se2.pi.dto.Community.CommunityPostDTOs;
import tn.esprit._4se2.pi.entities.Community;
import tn.esprit._4se2.pi.entities.Post;
import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.repositories.CommunityMemberRepository;
import tn.esprit._4se2.pi.repositories.CommunityRepository;
import tn.esprit._4se2.pi.repositories.PostRepository;
import tn.esprit._4se2.pi.repositories.CommentRepository;
import tn.esprit._4se2.pi.repositories.PostLikeRepository;
import tn.esprit._4se2.pi.repositories.UserRepository;
import tn.esprit._4se2.pi.services.Community.BadWordsFilterService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityPostService {

    private final PostRepository postRepository;
    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository communityMemberRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final PostLikeRepository postLikeRepository;
    private final BadWordsFilterService badWordsFilter;

    public CommunityPostDTOs.PostResponse createPost(Long communityId, CommunityPostDTOs.CreatePostRequest request) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Community not found"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = resolveCurrentUser(authentication);
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user is required");
        }

        boolean isAdmin = hasAdminAuthority(authentication);
        boolean isPlayer = hasPlayerAuthority(authentication);
        if (!isAdmin && !isPlayer) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only admins and players can create posts");
        }

        // ✅ Filtrer les bad words dans le contenu et le titre
        String filteredContent = badWordsFilter.filter(request.getContent().trim());
        String rawTitle = (request.getTitle() != null && !request.getTitle().isBlank())
                ? request.getTitle().trim()
                : deriveTitle(request.getContent());
        String filteredTitle = badWordsFilter.filter(rawTitle);

        Post post = new Post();
        post.setTitle(filteredTitle);
        post.setContent(filteredContent);
        post.setCreatedAt(LocalDateTime.now());
        post.setAuthor(currentUser);
        post.setCommunity(community);
        post.setCategory(community.getCategory());

        return toDTO(postRepository.save(post), currentUser.getId());
    }

    @Transactional(readOnly = true)
    public List<CommunityPostDTOs.PostResponse> getPostsForCommunity(Long communityId) {
        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Community not found"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = resolveCurrentUser(authentication);
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user is required");
        }

        Long viewerId = currentUser.getId();
        return postRepository.findByCommunity_IdOrderByCreatedAtDesc(community.getId())
                .stream()
                .map(p -> toDTO(p, viewerId))
                .collect(Collectors.toList());
    }

    private CommunityPostDTOs.PostResponse toDTO(Post post, Long viewerUserId) {
        int likesCount = postLikeRepository.countByPost_Id(post.getId());
        int commentsCount = commentRepository.countByPost_Id(post.getId());
        boolean liked = viewerUserId != null && postLikeRepository.existsByUser_IdAndPost_Id(viewerUserId, post.getId());
        String authorName = post.getAuthor() != null
                ? (post.getAuthor().getFirstName() + " " + post.getAuthor().getLastName()).trim()
                : null;
        return CommunityPostDTOs.PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .createdAt(post.getCreatedAt())
                .authorId(post.getAuthor() != null ? post.getAuthor().getId() : null)
                .authorFirstName(post.getAuthor() != null ? post.getAuthor().getFirstName() : null)
                .authorLastName(post.getAuthor() != null ? post.getAuthor().getLastName() : null)
                .authorName(authorName)
                .communityId(post.getCommunity() != null ? post.getCommunity().getId() : null)
                .communityName(post.getCommunity() != null ? post.getCommunity().getName() : null)
                .categoryId(post.getCategory() != null ? post.getCategory().getId() : null)
                .categoryName(post.getCategory() != null ? post.getCategory().getNom() : null)
                .likesCount(likesCount)
                .commentsCount(commentsCount)
                .likedByCurrentUser(liked)
                .build();
    }

    private String deriveTitle(String content) {
        if (content == null || content.isBlank()) return "";
        String firstLine = content.lines().map(String::trim).filter(l -> !l.isEmpty()).findFirst().orElse("");
        return firstLine.length() > 80 ? firstLine.substring(0, 77) + "..." : firstLine;
    }

    private boolean hasAdminAuthority(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities() == null) {
            return hasAdminRoleInDb(authentication);
        }
        boolean hasAuthority = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
        return hasAuthority || hasAdminRoleInDb(authentication);
    }

    private boolean hasPlayerAuthority(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities() == null) {
            return hasPlayerRoleInDb(authentication);
        }
        boolean hasAuthority = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_PLAYER"::equals);
        return hasAuthority || hasPlayerRoleInDb(authentication);
    }

    private boolean hasAdminRoleInDb(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            return false;
        }
        return userRepository.findByEmail(authentication.getName())
                .map(user -> user.getRole() == Role.ROLE_ADMIN)
                .orElse(false);
    }

    private boolean hasPlayerRoleInDb(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            return false;
        }
        return userRepository.findByEmail(authentication.getName())
                .map(user -> user.getRole() == Role.ROLE_PLAYER)
                .orElse(false);
    }

    private User resolveCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            return null;
        }
        return userRepository.findByEmail(authentication.getName()).orElse(null);
    }
}