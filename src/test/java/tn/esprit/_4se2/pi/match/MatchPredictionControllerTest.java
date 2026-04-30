package tn.esprit._4se2.pi.match;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tn.esprit._4se2.pi.dto.Match.MatchPredictionRequestDto;
import tn.esprit._4se2.pi.dto.Match.MatchPredictionResponseDto;
import tn.esprit._4se2.pi.restcontrollers.MatchPredictionController;
import tn.esprit._4se2.pi.services.Match.MatchPredictionService;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ─────────────────────────────────────────────────────────────
 *  TEST CONTRÔLEUR — MatchPredictionController
 *  (MockMvc : teste les endpoints REST sans démarrer le serveur)
 * ─────────────────────────────────────────────────────────────
 *  Vérifie :
 *   - POST /api/matches/predict/{matchId}  → 200 + JSON correct
 *   - POST /api/matches/predict/manual     → 200 + JSON correct
 *   - corps invalide                       → 400 Bad Request
 */
@WebMvcTest(MatchPredictionController.class)
@DisplayName("MatchPredictionController — Tests REST (MockMvc)")
class MatchPredictionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MatchPredictionService predictionService;

    // ── Helper ───────────────────────────────────────────────
    private MatchPredictionResponseDto fakeResponse(String result) {
        MatchPredictionResponseDto r = new MatchPredictionResponseDto();
        r.setResult(result);
        r.setConfidence(78.5);
        r.setProbabilities(Map.of(
                "HOME_WIN", 78.5,
                "AWAY_WIN", 14.2,
                "DRAW",      7.3
        ));
        r.setInterpretation("L'équipe domicile devrait gagner.");
        return r;
    }

    private MatchPredictionRequestDto validRequest() {
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
        return req;
    }

    // ═══════════════════════════════════════════════════════
    //  1. POST /api/matches/predict/{matchId}
    // ═══════════════════════════════════════════════════════
    @Test
    @DisplayName("POST /predict/{matchId} → 200 + résultat HOME_WIN")
    void testPredictById_returns200WithResult() throws Exception {
        when(predictionService.predictMatch(eq(10L)))
                .thenReturn(fakeResponse("HOME_WIN"));

        mockMvc.perform(post("/api/matches/predict/10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result",        is("HOME_WIN")))
                .andExpect(jsonPath("$.confidence",    is(78.5)))
                .andExpect(jsonPath("$.interpretation",notNullValue()))
                .andExpect(jsonPath("$.probabilities.HOME_WIN", is(78.5)));
    }

    @Test
    @DisplayName("POST /predict/{matchId} → 200 + résultat DRAW")
    void testPredictById_drawResult() throws Exception {
        when(predictionService.predictMatch(eq(5L)))
                .thenReturn(fakeResponse("DRAW"));

        mockMvc.perform(post("/api/matches/predict/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is("DRAW")));
    }

    // ═══════════════════════════════════════════════════════
    //  2. POST /api/matches/predict/manual
    // ═══════════════════════════════════════════════════════
    @Test
    @DisplayName("POST /predict/manual → 200 + résultat AWAY_WIN")
    void testPredictManual_returns200() throws Exception {
        MatchPredictionRequestDto req = validRequest();
        when(predictionService.predictFromDto(any(MatchPredictionRequestDto.class)))
                .thenReturn(fakeResponse("AWAY_WIN"));

        mockMvc.perform(post("/api/matches/predict/manual")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result",     is("AWAY_WIN")))
                .andExpect(jsonPath("$.confidence", is(78.5)));
    }

    @Test
    @DisplayName("POST /predict/manual → toutes les probabilités présentes dans la réponse")
    void testPredictManual_allProbabilitiesPresent() throws Exception {
        MatchPredictionRequestDto req = validRequest();
        when(predictionService.predictFromDto(any()))
                .thenReturn(fakeResponse("HOME_WIN"));

        mockMvc.perform(post("/api/matches/predict/manual")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(jsonPath("$.probabilities",    notNullValue()))
                .andExpect(jsonPath("$.probabilities.HOME_WIN", is(78.5)))
                .andExpect(jsonPath("$.probabilities.AWAY_WIN", is(14.2)))
                .andExpect(jsonPath("$.probabilities.DRAW",     is(7.3)));
    }

    // ═══════════════════════════════════════════════════════
    //  3. Gestion d'erreur — service lance une exception
    // ═══════════════════════════════════════════════════════
    @Test
    @DisplayName("POST /predict/{matchId} → 500 si service lance RuntimeException")
    void testPredictById_serviceError_returns500() throws Exception {
        when(predictionService.predictMatch(eq(999L)))
                .thenThrow(new RuntimeException("Match introuvable : 999"));

        mockMvc.perform(post("/api/matches/predict/999"))
                .andExpect(status().is5xxServerError());
    }
}
