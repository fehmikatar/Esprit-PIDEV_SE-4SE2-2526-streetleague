package tn.esprit._4se2.pi.services.TeamJoinRequest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import tn.esprit._4se2.pi.Enum.JoinRequestStatus;
import tn.esprit._4se2.pi.Enum.Role;
import tn.esprit._4se2.pi.entities.Team;
import tn.esprit._4se2.pi.entities.TeamJoinRequest;
import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.repositories.CommunityMemberRepository;
import tn.esprit._4se2.pi.repositories.CommunityRepository;
import tn.esprit._4se2.pi.repositories.TeamJoinRequestRepository;
import tn.esprit._4se2.pi.repositories.TeamMemberRepository;
import tn.esprit._4se2.pi.repositories.TeamRepository;
import tn.esprit._4se2.pi.repositories.UserRepository;
import tn.esprit._4se2.pi.services.chat.ChatService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamJoinRequestServiceTest {

    @Mock
    private TeamJoinRequestRepository teamJoinRequestRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private CommunityRepository communityRepository;
    @Mock
    private CommunityMemberRepository communityMemberRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ChatService chatService;

    @InjectMocks
    private TeamJoinRequestService teamJoinRequestService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void approveJoinRequestEnsuresTeamRoomExists() {
        User admin = User.builder().id(1L).email("admin@test.com").role(Role.ROLE_ADMIN).build();
        User member = User.builder().id(2L).email("member@test.com").build();
        Team team = Team.builder().id(10L).name("Team X").build();
        TeamJoinRequest request = TeamJoinRequest.builder()
                .id(100L)
                .team(team)
                .user(member)
                .status(JoinRequestStatus.PENDING)
                .build();

        authenticateAsAdmin("admin@test.com");

        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(admin));
        when(teamJoinRequestRepository.findById(100L)).thenReturn(Optional.of(request));
        when(teamMemberRepository.existsByTeam_IdAndUser_Id(10L, 2L)).thenReturn(false);

        var response = teamJoinRequestService.approveJoinRequest(100L);

        assertEquals("APPROVED", response.get("status"));
        verify(chatService).getOrCreateTeamRoom(10L);
        verify(teamMemberRepository).save(org.mockito.ArgumentMatchers.any());
    }

    private void authenticateAsAdmin(String email) {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(email, "pwd", "ROLE_ADMIN");
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }
}
