package tn.esprit._4se2.pi.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit._4se2.pi.dto.MatchingDTOs;
import tn.esprit._4se2.pi.entities.Player;
import tn.esprit._4se2.pi.entities.Team;
import tn.esprit._4se2.pi.repositories.MatchScoreRepository;
import tn.esprit._4se2.pi.repositories.PlayerRepository;
import tn.esprit._4se2.pi.repositories.TeamRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MatchingServiceTest {

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private MatchScoreRepository matchScoreRepository;

    @InjectMocks
    private MatchingService matchingService;

    private Player mockPlayer;
    private Team teamPerfect;
    private Team teamBad;

    @BeforeEach
    void setUp() {
        mockPlayer = new Player();
        mockPlayer.setId(1L);
        mockPlayer.setFirstName("John");
        mockPlayer.setLastName("Doe");
        mockPlayer.setSkillLevel(tn.esprit._4se2.pi.entities.enums.SkillLevel.INTERMEDIATE); // Intermediate
        mockPlayer.setPosition(tn.esprit._4se2.pi.entities.enums.PlayPosition.STRIKER);
        mockPlayer.setAvailability(new java.util.ArrayList<>());
        mockPlayer.setSportType(tn.esprit._4se2.pi.entities.enums.SportType.FOOTBALL);
        mockPlayer.setLatitude(48.8566);
        mockPlayer.setLongitude(2.3522); // Paris
        mockPlayer.setRating(4.5);

        teamPerfect = new Team();
        teamPerfect.setId(10L);
        teamPerfect.setName("Paris Strikers");
        teamPerfect.setLevel(tn.esprit._4se2.pi.entities.enums.SkillLevel.INTERMEDIATE);
        teamPerfect.setSport(tn.esprit._4se2.pi.entities.enums.SportType.FOOTBALL);
        teamPerfect.setRequiredPositions(new java.util.ArrayList<>());
        teamPerfect.setSchedule(new java.util.ArrayList<>());
        teamPerfect.setLatitude(48.8600);
        teamPerfect.setLongitude(2.3500); // Very close

        teamBad = new Team();
        teamBad.setId(11L);
        teamBad.setName("London Defenders");
        teamBad.setLevel(tn.esprit._4se2.pi.entities.enums.SkillLevel.BEGINNER);
        teamBad.setSport(tn.esprit._4se2.pi.entities.enums.SportType.FOOTBALL);
        teamBad.setRequiredPositions(new java.util.ArrayList<>());
        teamBad.setSchedule(new java.util.ArrayList<>());
        teamBad.setLatitude(51.5074);
        teamBad.setLongitude(-0.1278); // Very far (London)
    }

    @Test
    void testGetBestTeamsForPlayer_SortsByAlgorithmWeight() {
        when(playerRepository.findById(1L)).thenReturn(Optional.of(mockPlayer));
        when(teamRepository.findAll()).thenReturn(Arrays.asList(teamBad, teamPerfect));
        when(teamRepository.getReferenceById(anyLong())).thenReturn(new Team());

        List<MatchingDTOs.MatchResponse> recommendations = matchingService.getBestTeamsForPlayer(1L, 5);

        assertEquals(2, recommendations.size());

        // The perfect match should be rated higher and come first
        assertEquals("Paris Strikers", recommendations.get(0).getName());
        assertEquals("London Defenders", recommendations.get(1).getName());

        // Perfect team score should be quite high
        double perfectScore = recommendations.get(0).getScore();
        double badScore = recommendations.get(1).getScore();

        System.out.println("Perfect Team Score: " + perfectScore);
        System.out.println("Bad Team Score: " + badScore);

        // Sanity checks on the algorithm output
        assert perfectScore >= badScore;
        assert perfectScore > 50; 
        assert badScore < 100;
        
        // Assert we saved the match history twice (one for each match)
        verify(matchScoreRepository, times(2)).save(any());
    }
}
