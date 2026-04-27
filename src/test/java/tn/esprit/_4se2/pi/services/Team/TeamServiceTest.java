package tn.esprit._4se2.pi.services.Team;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import tn.esprit._4se2.pi.Enum.Role;
import tn.esprit._4se2.pi.Enum.TeamStatus;
import tn.esprit._4se2.pi.entities.Team;
import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.repositories.CategoryRepository;
import tn.esprit._4se2.pi.repositories.MessageRepository;
import tn.esprit._4se2.pi.repositories.TeamJoinRequestRepository;
import tn.esprit._4se2.pi.repositories.TeamMemberRepository;
import tn.esprit._4se2.pi.repositories.TeamRepository;
import tn.esprit._4se2.pi.repositories.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private TeamJoinRequestRepository teamJoinRequestRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;
    @Mock
    private MessageRepository messageRepository;
    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private TeamService teamService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createWithValidTeamSetsDefaultsAndCreator() {
        Team team = Team.builder().name("Street Legends").build();
        User creator = User.builder()
                .id(7L)
                .email("player@example.com")
                .role(Role.ROLE_PLAYER)
                .build();

        authenticate("player@example.com");
        when(userRepository.findByEmail("player@example.com")).thenReturn(Optional.of(creator));
        when(teamRepository.save(any(Team.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Team saved = teamService.create(team);

        assertNotNull(saved.getCreatedAt());
        assertEquals(TeamStatus.ACTIVE, saved.getStatus());
        assertSame(creator, saved.getCreatedBy());
        verify(teamRepository).save(team);
    }

    @Test
    void createWithoutNameThrowsException() {
        Team team = Team.builder().name(" ").build();

        assertThrows(IllegalArgumentException.class, () -> teamService.create(team));
        verifyNoInteractions(teamRepository);
    }

    @Test
    void getAllForNonAdminUsesSafeQuery() {
        User player = User.builder()
                .id(11L)
                .email("user@example.com")
                .role(Role.ROLE_PLAYER)
                .build();
        List<Team> expected = List.of(Team.builder().id(1L).name("Team A").build());

        authenticate("user@example.com");
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(player));
        when(teamRepository.findAllSafe()).thenReturn(expected);

        List<Team> actual = teamService.getAll();

        assertEquals(expected, actual);
        verify(teamRepository).findAllSafe();
        verify(teamRepository, never()).findAll();
    }

    @Test
    void deleteExistingTeamRemovesDependenciesAndTeam() {
        Long teamId = 9L;
        when(teamRepository.existsById(teamId)).thenReturn(true);
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class), eq("team_members"))).thenReturn(1);

        teamService.delete(teamId);

        verify(messageRepository).deleteByTeam_Id(teamId);
        verify(teamJoinRequestRepository).deleteByTeam_Id(teamId);
        verify(teamMemberRepository).deleteByTeam_Id(teamId);
        verify(jdbcTemplate).update("DELETE FROM team_members WHERE team_id = ?", teamId);
        verify(teamRepository).deleteById(teamId);
    }

    @Test
    void archiveDormantTeamsUsesMinimumOneDay() {
        when(teamRepository.archiveDormantTeams(any(LocalDateTime.class))).thenReturn(2);

        int archived = teamService.archiveDormantTeams(0);

        ArgumentCaptor<LocalDateTime> captor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(teamRepository).archiveDormantTeams(captor.capture());
        assertEquals(2, archived);
        assertTrue(captor.getValue().isBefore(LocalDateTime.now()));
        assertTrue(captor.getValue().isAfter(LocalDateTime.now().minusDays(2)));
    }

    private void authenticate(String email, String... authorities) {
        TestingAuthenticationToken authentication = new TestingAuthenticationToken(email, "pwd", authorities);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }
}