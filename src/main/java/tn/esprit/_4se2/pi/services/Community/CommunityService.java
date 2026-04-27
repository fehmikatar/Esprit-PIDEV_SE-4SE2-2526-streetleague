package tn.esprit._4se2.pi.services.Community;

import lombok.RequiredArgsConstructor;
<<<<<<< Updated upstream
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
=======
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.Enum.Role;
import tn.esprit._4se2.pi.dto.Community.CommunityDTO;
import tn.esprit._4se2.pi.entities.Category;
import tn.esprit._4se2.pi.entities.Community;
import tn.esprit._4se2.pi.entities.CommunityMember;
import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.repositories.CategoryRepository;
import tn.esprit._4se2.pi.repositories.CommunityMemberRepository;
import tn.esprit._4se2.pi.repositories.CommunityRepository;
import tn.esprit._4se2.pi.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
>>>>>>> Stashed changes
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
<<<<<<< Updated upstream
public class CommunityService implements ICommunityService {

    private final CommunityPostRepository postRepository;
    private final CommunityCommentRepository commentRepository;
    private final PostLikeRepository likeRepository;
    private final SportCommunityRepository sportCommunityRepository;
    private final CommunityMemberRepository communityMemberRepository;
    private final CategoryRepository categoryRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public List<SportCommunityResponse> getMyCommunities(Long userId) {
        User user = findUserOrThrow(userId);

        if (user.getRole() == Role.ROLE_ADMIN) {
            // Ensure a community exists for each sport category so admin always sees all communities.
            categoryRepository.findAll().forEach(category ->
                    sportCommunityRepository.findBySportCategoryId(category.getId())
                            .orElseGet(() -> sportCommunityRepository.save(
                                    SportCommunity.builder()
                                            .name(category.getNom() + " Community")
                                            .sportCategory(category)
                                            .createdAt(LocalDateTime.now())
                                            .build()
                            ))
            );

            return sportCommunityRepository.findAll()
                    .stream()
                    .map(this::mapToSportCommunityResponse)
                    .collect(Collectors.toList());
        }

        // Backfill community membership from existing active team memberships (legacy data compatibility).
        teamMemberRepository.findAllByUserIdAndStatus(userId, MemberStatus.ACTIVE)
                .forEach(member -> {
                    Team team = member.getTeam();
                    if (team == null || team.getSport() == null || team.getSport().isBlank()) {
                        return;
                    }

                    try {
                        SportCommunity community = ensureCommunityForSport(team.getSport());
                        if (!communityMemberRepository.existsByCommunityIdAndUserId(community.getId(), userId)) {
                            CommunityMember autoMember = CommunityMember.builder()
                                    .community(community)
                                    .user(user)
                                    .joinedAt(LocalDateTime.now())
                                    .role(CommunityMemberRole.MEMBER)
                                    .build();
                            communityMemberRepository.save(autoMember);
                        }
                    } catch (ResourceNotFoundException ignored) {
                        // Skip invalid legacy sport values that no longer match a category.
                    }
                });

        return communityMemberRepository.findAllByUserIdOrderByJoinedAtDesc(userId)
                .stream()
                .map(CommunityMember::getCommunity)
                .distinct()
                .map(this::mapToSportCommunityResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommunityPostResponse> getCommunityPosts(Long communityId, Long authenticatedUserId) {
        SportCommunity community = findCommunityOrThrow(communityId);
        assertCommunityMember(communityId, authenticatedUserId);

        return postRepository.findByCommunityIdAndStatusOrderByCreatedAtDesc(community.getId(), PostStatus.VISIBLE)
                .stream()
                .map(p -> mapToPostResponse(p, authenticatedUserId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommunityPostResponse createCommunityPost(Long communityId, CommunityPostRequest request, Long userId) {
        SportCommunity community = findCommunityOrThrow(communityId);
        assertCommunityMember(communityId, userId);
        User author = findUserOrThrow(userId);

        CommunityPost post = CommunityPost.builder()
                .author(author)
                .community(community)
                .content(request.getContent())
                .postType(request.getPostType() != null ? request.getPostType() : PostType.GENERAL)
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
    public List<CommunityMemberResponse> getCommunityMembers(Long communityId, Long authenticatedUserId) {
        assertCommunityMember(communityId, authenticatedUserId);

        return communityMemberRepository.findAllByCommunityIdOrderByJoinedAtAsc(communityId)
                .stream()
                .map(this::mapToCommunityMemberResponse)
                .collect(Collectors.toList());
    }

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

            // TEAM community is shared by sport category: any active member of a team in the same sport can post.
            if (!canAccessSportCommunity(team.getSport(), userId)) {
                throw new ForbiddenException("You must be a member of this sport community to post in it.");
            }
        }

        SportCommunity community = null;
        if (team != null) {
            community = ensureCommunityForSport(team.getSport());
        }

        CommunityPost post = CommunityPost.builder()
                .author(author)
                .content(request.getContent())
                .postType(request.getPostType())
                .team(team)
                .community(community)
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
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));

        if (!canAccessSportCommunity(team.getSport(), authenticatedUserId)) {
            throw new ForbiddenException("You must be a member of this sport community to view posts.");
        }

        SportCommunity community = ensureCommunityForSport(team.getSport());
        assertCommunityMember(community.getId(), authenticatedUserId);

        return postRepository.findByCommunityIdAndStatusOrderByCreatedAtDesc(community.getId(), PostStatus.VISIBLE)
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
        assertPostAccessible(post, userId);
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
    public List<CommunityCommentResponse> getComments(Long postId, Long authenticatedUserId) {
        CommunityPost post = findPostOrThrow(postId);
        assertPostAccessible(post, authenticatedUserId);
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
        assertPostAccessible(post, userId);
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

    private SportCommunity findCommunityOrThrow(Long communityId) {
        return sportCommunityRepository.findById(communityId)
                .orElseThrow(() -> new ResourceNotFoundException("Community not found with id: " + communityId));
    }

    private SportCommunity ensureCommunityForSport(String sport) {
        Category category = categoryRepository.findByNomIgnoreCase(sport)
                .orElseThrow(() -> new ResourceNotFoundException("Sport category not found: " + sport));

        return sportCommunityRepository.findBySportCategoryId(category.getId())
                .orElseGet(() -> sportCommunityRepository.save(
                        SportCommunity.builder()
                                .name(category.getNom() + " Community")
                                .sportCategory(category)
                                .createdAt(LocalDateTime.now())
                                .build()
                ));
    }

    private void assertCommunityMember(Long communityId, Long userId) {
        User user = findUserOrThrow(userId);
        if (user.getRole() == Role.ROLE_ADMIN) {
            return;
        }

        if (!communityMemberRepository.existsByCommunityIdAndUserId(communityId, userId)) {
            throw new ForbiddenException("You must be a member of this community.");
        }
    }

    private CommunityPostResponse mapToPostResponse(CommunityPost post, Long authenticatedUserId) {
        boolean isLiked = authenticatedUserId != null &&
                likeRepository.existsByPostIdAndUserId(post.getId(), authenticatedUserId);

        User author = post.getAuthor();
        Team team = post.getTeam();
        SportCommunity community = post.getCommunity();

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
                .communityId(community != null ? community.getId() : null)
                .communityName(community != null ? community.getName() : null)
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

    private SportCommunityResponse mapToSportCommunityResponse(SportCommunity community) {
        return SportCommunityResponse.builder()
                .id(community.getId())
                .name(community.getName())
                .sportCategoryId(community.getSportCategory().getId())
                .sportCategory(community.getSportCategory().getNom())
                .createdAt(community.getCreatedAt())
                .membersCount(community.getMembers() != null ? community.getMembers().size() : 0)
                .postsCount(community.getPosts() != null ? community.getPosts().size() : 0)
                .build();
    }

    private CommunityMemberResponse mapToCommunityMemberResponse(CommunityMember member) {
        User user = member.getUser();
        return CommunityMemberResponse.builder()
                .id(member.getId())
                .userId(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(member.getRole())
                .joinedAt(member.getJoinedAt())
                .build();
    }

    private boolean canAccessSportCommunity(String sport, Long userId) {
        if (sport == null || sport.isBlank()) {
            return false;
        }

        User user = findUserOrThrow(userId);
        if (user.getRole() == Role.ROLE_ADMIN) {
            return true;
        }

        return teamMemberRepository.existsByUserIdAndStatusAndTeamSport(
                userId,
                MemberStatus.ACTIVE,
                sport
        );
    }

    private void assertPostAccessible(CommunityPost post, Long userId) {
        if (post.getCommunity() != null) {
            assertCommunityMember(post.getCommunity().getId(), userId);
            return;
        }

        Team team = post.getTeam();
        if (team != null && !canAccessSportCommunity(team.getSport(), userId)) {
            throw new ForbiddenException("You must be a member of this sport community to access this post.");
        }
    }
=======
@Transactional
public class CommunityService {

	private final CommunityRepository communityRepository;
	private final CommunityMemberRepository communityMemberRepository;
	private final CategoryRepository categoryRepository;
	private final UserRepository userRepository;

	public Community ensureCommunityForCategory(Category category) {
		if (category == null) {
			return null;
		}

		Optional<Community> existing = communityRepository.findByCategory_Id(category.getId());
		if (existing.isPresent()) {
			Community community = existing.get();
			community.setName(category.getNom());
			community.setDescription(category.getDescription());
			return communityRepository.save(community);
		}

		Community community = Community.builder()
				.name(category.getNom())
				.description(category.getDescription())
				.category(category)
				.createdAt(LocalDateTime.now())
				.build();
		return communityRepository.save(community);
	}

	public void syncCommunityWithCategory(Category category) {
		ensureCommunityForCategory(category);
	}

	public void deleteByCategoryId(Long categoryId) {
		communityRepository.findByCategory_Id(categoryId).ifPresent(communityRepository::delete);
	}

	public void addUserToCommunityByCategoryId(Long categoryId, User user) {
		if (categoryId == null || user == null) {
			return;
		}
		communityRepository.findByCategory_Id(categoryId).ifPresent(community -> addUserToCommunity(community, user));
	}

	public void addUserToCommunity(Community community, User user) {
		if (community == null || user == null) {
			return;
		}
		if (communityMemberRepository.existsByCommunity_IdAndUser_Id(community.getId(), user.getId())) {
			return;
		}
		communityMemberRepository.save(CommunityMember.builder()
				.community(community)
				.user(user)
				.joinedAt(LocalDateTime.now())
				.build());
	}

	@Transactional(readOnly = true)
	public List<CommunityDTO> getVisibleCommunitiesForCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (isAdmin(authentication)) {
			return communityRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
		}

		User currentUser = resolveCurrentUser(authentication);
		if (currentUser == null) {
			return List.of();
		}

		return communityMemberRepository.findByUser_Id(currentUser.getId()).stream()
				.map(CommunityMember::getCommunity)
				.distinct()
				.map(this::toDTO)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public Optional<CommunityDTO> getVisibleCommunityById(Long id) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<Community> community = communityRepository.findById(id);
		if (community.isEmpty()) {
			return Optional.empty();
		}

		if (isAdmin(authentication)) {
			return community.map(this::toDTO);
		}

		User currentUser = resolveCurrentUser(authentication);
		if (currentUser == null) {
			return Optional.empty();
		}

		boolean allowed = communityMemberRepository.existsByCommunity_IdAndUser_Id(id, currentUser.getId());
		return allowed ? community.map(this::toDTO) : Optional.empty();
	}

	@Transactional(readOnly = true)
	public List<CommunityDTO> getCommunityInsightsByCategory(String categoryKeyword, Long minMembers) {
		long safeMinMembers = minMembers == null || minMembers < 0 ? 0 : minMembers;
		String safeKeyword = categoryKeyword == null ? "" : categoryKeyword.trim();
		return communityRepository.findCommunityInsightsByCategory(safeKeyword, safeMinMembers);
	}

	private CommunityDTO toDTO(Community community) {
		return CommunityDTO.builder()
				.id(community.getId())
				.name(community.getName())
				.description(community.getDescription())
				.categoryId(community.getCategory() != null ? community.getCategory().getId() : null)
				.categoryName(community.getCategory() != null ? community.getCategory().getNom() : null)
				.membersCount((long) (community.getMembers() != null ? community.getMembers().size() : 0))
				.createdAt(community.getCreatedAt())
				.build();
	}

	private boolean isAdmin(Authentication authentication) {
		if (authentication == null || authentication.getAuthorities() == null) {
			return false;
		}
		boolean hasAuthority = authentication.getAuthorities().stream()
				.anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
		User currentUser = resolveCurrentUser(authentication);
		return hasAuthority || (currentUser != null && currentUser.getRole() == Role.ROLE_ADMIN);
	}

	private User resolveCurrentUser(Authentication authentication) {
		if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
			return null;
		}
		return userRepository.findByEmail(authentication.getName()).orElse(null);
	}
>>>>>>> Stashed changes
}
