package tn.esprit._4se2.pi.services.chat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import tn.esprit._4se2.pi.dto.chat.ChatMessageDTO;
import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.repositories.ChatMessageRepository;
import tn.esprit._4se2.pi.repositories.ChatRoomRepository;
import tn.esprit._4se2.pi.repositories.CommunityRepository;
import tn.esprit._4se2.pi.repositories.TeamMemberRepository;
import tn.esprit._4se2.pi.repositories.TeamRepository;
import tn.esprit._4se2.pi.repositories.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceAuthorizationTest {

    @Mock
    private ChatMessageRepository chatMessageRepository;
    @Mock
    private ChatRoomRepository chatRoomRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TeamRepository teamRepository;
    @Mock
    private CommunityRepository communityRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private ChatService chatService;

    @Test
    void validateTeamRoomAccessAllowsApprovedMember() {
        when(teamMemberRepository.existsByTeam_IdAndUser_Id(12L, 3L)).thenReturn(true);

        assertDoesNotThrow(() -> chatService.validateTeamRoomAccess("team_12", 3L));

        verify(teamMemberRepository).existsByTeam_IdAndUser_Id(12L, 3L);
    }

    @Test
    void validateTeamRoomAccessDeniesNonMember() {
        when(teamMemberRepository.existsByTeam_IdAndUser_Id(12L, 3L)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> chatService.validateTeamRoomAccess("team_12", 3L));

        verify(teamMemberRepository).existsByTeam_IdAndUser_Id(12L, 3L);
    }

    @Test
    void saveMessageRejectsMismatchedTeamPayloadAndRoom() {
        User sender = User.builder().id(5L).email("member@test.com").build();
        when(userRepository.findByEmail("member@test.com")).thenReturn(Optional.of(sender));

        ChatMessageDTO message = ChatMessageDTO.builder()
                .roomType("TEAM")
                .roomId("team_8")
                .teamId(9L)
                .content("hello")
                .build();

        assertThrows(IllegalArgumentException.class, () -> chatService.saveMessage(message, "member@test.com"));

        verifyNoInteractions(teamMemberRepository);
    }

    @Test
    void saveMessageDeniesWhenUserNotApprovedMember() {
        User sender = User.builder().id(5L).email("member@test.com").build();
        when(userRepository.findByEmail("member@test.com")).thenReturn(Optional.of(sender));
        when(teamMemberRepository.existsByTeam_IdAndUser_Id(8L, 5L)).thenReturn(false);

        ChatMessageDTO message = ChatMessageDTO.builder()
                .roomType("TEAM")
                .roomId("team_8")
                .content("hello")
                .build();

        assertThrows(AccessDeniedException.class, () -> chatService.saveMessage(message, "member@test.com"));

        verify(teamMemberRepository).existsByTeam_IdAndUser_Id(8L, 5L);
    }
}
