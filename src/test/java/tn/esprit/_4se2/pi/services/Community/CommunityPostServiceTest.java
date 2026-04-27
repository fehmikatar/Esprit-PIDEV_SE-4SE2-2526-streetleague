package tn.esprit._4se2.pi.services.Community;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit._4se2.pi.Enum.Role;
import tn.esprit._4se2.pi.dto.Community.CommunityPostDTOs;
import tn.esprit._4se2.pi.entities.Category;
import tn.esprit._4se2.pi.entities.Community;
import tn.esprit._4se2.pi.entities.Post;
import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.repositories.CommunityMemberRepository;
import tn.esprit._4se2.pi.repositories.CommunityRepository;
import tn.esprit._4se2.pi.repositories.PostRepository;
import tn.esprit._4se2.pi.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommunityPostServiceTest {

    @Mock
    private PostRepository postRepository;
    @Mock
    private CommunityRepository communityRepository;
    @Mock
    private CommunityMemberRepository communityMemberRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommunityPostService communityPostService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createPostSucceedsForPlayerAndTrimsContent() {
        Category category = Category.builder().id(6L).nom("Football").build();
        Community community = Community.builder().id(3L).name("Football Hub").category(category).build();
        User player = User.builder()
                .id(10L)
                .email("player@example.com")
                .firstName("Ali")
                .lastName("Ben")
                .role(Role.ROLE_PLAYER)
                .build();

        CommunityPostDTOs.CreatePostRequest request = CommunityPostDTOs.CreatePostRequest.builder()
                .title("  Title  ")
                .content("  Content  ")
                .build();

        authenticate("player@example.com", "ROLE_PLAYER");
        when(communityRepository.findById(3L)).thenReturn(Optional.of(community));
        when(userRepository.findByEmail("player@example.com")).thenReturn(Optional.of(player));
        when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
            Post p = invocation.getArgument(0);
            p.setId(100L);
            return p;
        });

        CommunityPostDTOs.PostResponse result = communityPostService.createPost(3L, request);

        assertEquals(100L, result.getId());
        assertEquals("Title", result.getTitle());
        assertEquals("Content", result.getContent());
        assertEquals(3L, result.getCommunityId());
        assertEquals("Football Hub", result.getCommunityName());
        assertEquals(6L, result.getCategoryId());
        assertEquals("Football", result.getCategoryName());
        assertEquals(10L, result.getAuthorId());
        assertEquals("Ali", result.getAuthorFirstName());
        assertNotNull(result.getCreatedAt());
    }

    @Test
    void createPostThrowsNotFoundWhenCommunityMissing() {
        CommunityPostDTOs.CreatePostRequest request = CommunityPostDTOs.CreatePostRequest.builder()
                .title("Title")
                .content("Content")
                .build();

        when(communityRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> communityPostService.createPost(999L, request));

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void createPostThrowsForbiddenWhenRoleNotAllowed() {
        Category category = Category.builder().id(1L).nom("Basket").build();
        Community community = Community.builder().id(2L).name("Basket Hub").category(category).build();
        User fieldOwner = User.builder()
                .id(20L)
                .email("owner@example.com")
                .role(Role.ROLE_FIELD_OWNER)
                .build();

        CommunityPostDTOs.CreatePostRequest request = CommunityPostDTOs.CreatePostRequest.builder()
                .title("Title")
                .content("Content")
                .build();

        authenticate("owner@example.com");
        when(communityRepository.findById(2L)).thenReturn(Optional.of(community));
        when(userRepository.findByEmail("owner@example.com")).thenReturn(Optional.of(fieldOwner));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> communityPostService.createPost(2L, request));

        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
    }

    @Test
    void getPostsForCommunityReturnsMappedPostsForAuthenticatedUser() {
        Category category = Category.builder().id(1L).nom("Tennis").build();
        Community community = Community.builder().id(50L).name("Tennis Hub").category(category).build();
        User user = User.builder().id(40L).email("player2@example.com").firstName("Sara").lastName("A").role(Role.ROLE_PLAYER).build();

        Post post = new Post();
        post.setId(9L);
        post.setTitle("Hello");
        post.setContent("World");
        post.setCreatedAt(LocalDateTime.now());
        post.setAuthor(user);
        post.setCommunity(community);
        post.setCategory(category);

        authenticate("player2@example.com");
        when(communityRepository.findById(50L)).thenReturn(Optional.of(community));
        when(userRepository.findByEmail("player2@example.com")).thenReturn(Optional.of(user));
        when(postRepository.findByCommunity_IdOrderByCreatedAtDesc(50L)).thenReturn(List.of(post));

        List<CommunityPostDTOs.PostResponse> result = communityPostService.getPostsForCommunity(50L);

        assertEquals(1, result.size());
        assertEquals(9L, result.get(0).getId());
        assertEquals("Hello", result.get(0).getTitle());
        assertEquals("Tennis Hub", result.get(0).getCommunityName());
    }

    @Test
    void getPostsForCommunityThrowsUnauthorizedWithoutResolvedUser() {
        Category category = Category.builder().id(2L).nom("Volley").build();
        Community community = Community.builder().id(70L).name("Volley Hub").category(category).build();

        authenticate("unknown@example.com");
        when(communityRepository.findById(70L)).thenReturn(Optional.of(community));
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> communityPostService.getPostsForCommunity(70L));

        assertEquals(HttpStatus.UNAUTHORIZED, ex.getStatusCode());
        verify(postRepository, org.mockito.Mockito.never()).findByCommunity_IdOrderByCreatedAtDesc(any(Long.class));
    }

    private void authenticate(String email, String... authorities) {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(email, "pwd", authorities);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }
}