package tn.esprit._4se2.pi.services.Community;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommunityServiceTest {

    @Mock
    private CommunityRepository communityRepository;
    @Mock
    private CommunityMemberRepository communityMemberRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CommunityService communityService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void ensureCommunityForCategoryUpdatesExistingCommunity() {
        Category category = Category.builder().id(3L).nom("Basket").description("all about basket").build();
        Community existing = Community.builder().id(8L).name("Old Name").description("Old Desc").category(category).build();

        when(communityRepository.findByCategory_Id(3L)).thenReturn(Optional.of(existing));
        when(communityRepository.save(existing)).thenReturn(existing);

        Community result = communityService.ensureCommunityForCategory(category);

        assertSame(existing, result);
        assertEquals("Basket", existing.getName());
        assertEquals("all about basket", existing.getDescription());
        verify(communityRepository).save(existing);
    }

    @Test
    void addUserToCommunitySavesMemberWhenNotAlreadyJoined() {
        Community community = Community.builder().id(4L).name("Football Hub").build();
        User user = User.builder().id(10L).email("player@example.com").build();

        when(communityMemberRepository.existsByCommunity_IdAndUser_Id(4L, 10L)).thenReturn(false);

        communityService.addUserToCommunity(community, user);

        ArgumentCaptor<CommunityMember> captor = ArgumentCaptor.forClass(CommunityMember.class);
        verify(communityMemberRepository).save(captor.capture());
        assertSame(community, captor.getValue().getCommunity());
        assertSame(user, captor.getValue().getUser());
        assertNotNull(captor.getValue().getJoinedAt());
    }

    @Test
    void getVisibleCommunitiesForCurrentUserReturnsAllWhenAdmin() {
        Category category = Category.builder().id(2L).nom("Football").build();
        Community community = Community.builder()
                .id(1L)
                .name("Football Community")
                .description("desc")
                .category(category)
                .createdAt(LocalDateTime.now())
                .build();

        authenticate("admin@example.com", "ROLE_ADMIN");
        when(communityRepository.findAll()).thenReturn(List.of(community));

        List<CommunityDTO> result = communityService.getVisibleCommunitiesForCurrentUser();

        assertEquals(1, result.size());
        assertEquals("Football Community", result.get(0).getName());
        assertEquals("Football", result.get(0).getCategoryName());
        verify(communityRepository).findAll();
        verify(communityMemberRepository, never()).findByUser_Id(anyLong());
    }

    @Test
    void getVisibleCommunityByIdReturnsEmptyWhenUserNotMember() {
        Long communityId = 7L;
        User currentUser = User.builder()
                .id(20L)
                .email("player@example.com")
                .role(Role.ROLE_PLAYER)
                .build();
        Category category = Category.builder().id(5L).nom("Tennis").build();
        Community community = Community.builder().id(communityId).name("Tennis Community").category(category).build();

        authenticate("player@example.com");
        when(communityRepository.findById(communityId)).thenReturn(Optional.of(community));
        when(userRepository.findByEmail("player@example.com")).thenReturn(Optional.of(currentUser));
        when(communityMemberRepository.existsByCommunity_IdAndUser_Id(communityId, 20L)).thenReturn(false);

        Optional<CommunityDTO> result = communityService.getVisibleCommunityById(communityId);

        assertTrue(result.isEmpty());
    }

    private void authenticate(String email, String... authorities) {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(email, "pwd", authorities);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }
}