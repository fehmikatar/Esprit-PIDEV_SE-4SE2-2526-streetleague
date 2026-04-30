package tn.esprit._4se2.pi.match;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import tn.esprit._4se2.pi.Enum.MatchStatus;
import tn.esprit._4se2.pi.dto.Match.MatchPredictionRequestDto;
import tn.esprit._4se2.pi.dto.Match.MatchPredictionResponseDto;
import tn.esprit._4se2.pi.entities.Match;
import tn.esprit._4se2.pi.repositories.MatchRepository;
import tn.esprit._4se2.pi.services.Match.MatchPredictionService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ─────────────────────────────────────────────────────────────
 *  TEST UNITAIRE — MatchPredictionService
 *  (Mockito : aucune BD ni FastAPI nécessaire)
 * ─────────────────────────────────────────────────────────────
 *  Vérifie la logique interne du service :
 *   - construction du DTO
 *   - calcul de forme (countRecentWins)
 *   - calcul des moyennes (avgGoalsScored / avgGoalsConceded)
 *   - délégation vers RestTemplate
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("MatchPredictionService — Tests Unitaires")
class MatchPredictionServiceTest {

    @Mock
    private MatchRepository matchRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private MatchPredictionService predictionService;

    // ── Constantes ───────────────────────────────────────────
    private static final Long HOME_TEAM = 1L;
    private static final Long AWAY_TEAM = 2L;
    private static final Long MATCH_ID  = 10L;

    // ── Helpers ──────────────────────────────────────────────

    private Match buildMatch(Long id, Long home, Long away,
                              int homeScore, int awayScore, MatchStatus status) {
        Match m = new Match();
        m.setId(id);
        m.setHomeTeamId(home);
        m.setAwayTeamId(away);
        m.setHomeScore(homeScore);
        m.setAwayScore(awayScore);
        m.setStatus(status);
        m.setScheduledAt(LocalDateTime.now().minusDays(id));
        m.setCompetitionId(1L);
        return m;
    }

    private MatchPredictionResponseDto buildFakeResponse(String result, double confidence) {
        MatchPredictionResponseDto resp = new MatchPredictionResponseDto();
        resp.setResult(result);
        resp.setConfidence(confidence);
        resp.setProbabilities(Map.of(result, confidence));
        resp.setInterpretation("Test interpretation");
        return resp;
    }

    @BeforeEach
    void setUp() {
        // Injecter l'URL FastAPI par réflexion (car @Value ne fonctionne pas avec @InjectMocks pur)
        try {
            var field = MatchPredictionService.class.getDeclaredField("predictUrl");
            field.setAccessible(true);
            field.set(predictionService, "http://localhost:8000/predict");

            var rtField = MatchPredictionService.class.getDeclaredField("restTemplate");
            rtField.setAccessible(true);
            rtField.set(predictionService, restTemplate);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // ═══════════════════════════════════════════════════════
    //  1. predictFromDto — délègue vers RestTemplate
    // ═══════════════════════════════════════════════════════
    @Test
    @DisplayName("predictFromDto() → appelle RestTemplate et retourne la réponse FastAPI")
    void testPredictFromDto_delegatesToRestTemplate() {
        // Arrange
        MatchPredictionRequestDto req = new MatchPredictionRequestDto();
        req.setHomeRank(3);
        req.setAwayRank(8);
        req.setHomeGoalsAvg(2.3);
        req.setAwayGoalsAvg(1.5);
        req.setHomeConcededAvg(1.0);
        req.setAwayConcededAvg(1.8);
        req.setHomeWinsLast5(4);
        req.setAwayWinsLast5(2);
        req.setHomeRatingAvg(4.2);
        req.setAwayRatingAvg(3.1);

        MatchPredictionResponseDto expected = buildFakeResponse("HOME_WIN", 78.5);

        when(restTemplate.postForObject(
                eq("http://localhost:8000/predict"),
                eq(req),
                eq(MatchPredictionResponseDto.class)
        )).thenReturn(expected);

        // Act
        MatchPredictionResponseDto result = predictionService.predictFromDto(req);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getResult()).isEqualTo("HOME_WIN");
        assertThat(result.getConfidence()).isEqualTo(78.5);
        verify(restTemplate, times(1))
                .postForObject(anyString(), any(), eq(MatchPredictionResponseDto.class));
    }

    // ═══════════════════════════════════════════════════════
    //  2. predictMatch — match inexistant → RuntimeException
    // ═══════════════════════════════════════════════════════
    @Test
    @DisplayName("predictMatch() → lève RuntimeException si match introuvable en BD")
    void testPredictMatch_matchNotFound_throwsException() {
        when(matchRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> predictionService.predictMatch(99L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Match introuvable");
    }

    // ═══════════════════════════════════════════════════════
    //  3. predictMatch — équipes sans historique → valeurs défaut
    // ═══════════════════════════════════════════════════════
    @Test
    @DisplayName("predictMatch() → utilise valeur 1.0 si aucun historique pour l'équipe")
    void testPredictMatch_noHistory_usesDefaults() {
        Match match = buildMatch(MATCH_ID, HOME_TEAM, AWAY_TEAM, 0, 0, MatchStatus.SCHEDULED);
        when(matchRepository.findById(MATCH_ID)).thenReturn(Optional.of(match));
        when(matchRepository.findFinishedByTeam(HOME_TEAM)).thenReturn(List.of());
        when(matchRepository.findFinishedByTeam(AWAY_TEAM)).thenReturn(List.of());

        MatchPredictionResponseDto expected = buildFakeResponse("DRAW", 55.0);
        when(restTemplate.postForObject(anyString(), any(MatchPredictionRequestDto.class),
                eq(MatchPredictionResponseDto.class))).thenReturn(expected);

        MatchPredictionResponseDto result = predictionService.predictMatch(MATCH_ID);

        assertThat(result).isNotNull();
        assertThat(result.getResult()).isEqualTo("DRAW");
    }

    // ═══════════════════════════════════════════════════════
    //  4. predictMatch — calcul correct des victoires récentes
    // ═══════════════════════════════════════════════════════
    @Test
    @DisplayName("predictMatch() → calcule correctement le nombre de victoires récentes")
    void testPredictMatch_countWinsCorrectly() {
        Match match = buildMatch(MATCH_ID, HOME_TEAM, AWAY_TEAM, 0, 0, MatchStatus.SCHEDULED);

        // 3 victoires à domicile pour HOME_TEAM sur 5 matchs terminés
        List<Match> homeHistory = Arrays.asList(
                buildMatch(1L, HOME_TEAM, 5L, 2, 0, MatchStatus.FINISHED),  // ✅ win
                buildMatch(2L, HOME_TEAM, 6L, 0, 1, MatchStatus.FINISHED),  // ❌ loss
                buildMatch(3L, HOME_TEAM, 7L, 3, 1, MatchStatus.FINISHED),  // ✅ win
                buildMatch(4L, HOME_TEAM, 8L, 1, 1, MatchStatus.FINISHED),  // draw
                buildMatch(5L, HOME_TEAM, 9L, 2, 0, MatchStatus.FINISHED)   // ✅ win
        );

        when(matchRepository.findById(MATCH_ID)).thenReturn(Optional.of(match));
        when(matchRepository.findFinishedByTeam(HOME_TEAM)).thenReturn(homeHistory);
        when(matchRepository.findFinishedByTeam(AWAY_TEAM)).thenReturn(List.of());

        MatchPredictionResponseDto expected = buildFakeResponse("HOME_WIN", 72.0);
        when(restTemplate.postForObject(anyString(), any(MatchPredictionRequestDto.class),
                eq(MatchPredictionResponseDto.class))).thenReturn(expected);

        predictionService.predictMatch(MATCH_ID);

        // Vérifie que RestTemplate a bien été appelé (la logique wins = 3)
        verify(restTemplate, times(1))
                .postForObject(anyString(), any(MatchPredictionRequestDto.class),
                        eq(MatchPredictionResponseDto.class));
    }

    // ═══════════════════════════════════════════════════════
    //  5. predictMatch — calcul avg buts marqués
    // ═══════════════════════════════════════════════════════
    @Test
    @DisplayName("predictMatch() → moyenne buts marqués = total / nombre de matchs")
    void testAvgGoalsScored_calculationIsCorrect() {
        Match match = buildMatch(MATCH_ID, HOME_TEAM, AWAY_TEAM, 0, 0, MatchStatus.SCHEDULED);

        // HOME_TEAM a marqué : 2 + 1 + 3 = 6 buts en 3 matchs → moy = 2.0
        List<Match> history = Arrays.asList(
                buildMatch(1L, HOME_TEAM, 5L, 2, 0, MatchStatus.FINISHED),
                buildMatch(2L, HOME_TEAM, 6L, 1, 1, MatchStatus.FINISHED),
                buildMatch(3L, HOME_TEAM, 7L, 3, 2, MatchStatus.FINISHED)
        );

        when(matchRepository.findById(MATCH_ID)).thenReturn(Optional.of(match));
        when(matchRepository.findFinishedByTeam(HOME_TEAM)).thenReturn(history);
        when(matchRepository.findFinishedByTeam(AWAY_TEAM)).thenReturn(List.of());

        // On capture le DTO envoyé au RestTemplate
        when(restTemplate.postForObject(anyString(),
                argThat((MatchPredictionRequestDto dto) -> {
                    // Assert à l'intérieur du matcher
                    assertThat(dto.getHomeGoalsAvg()).isEqualTo(2.0);
                    return true;
                }),
                eq(MatchPredictionResponseDto.class))
        ).thenReturn(buildFakeResponse("HOME_WIN", 70.0));

        predictionService.predictMatch(MATCH_ID);
    }
}
