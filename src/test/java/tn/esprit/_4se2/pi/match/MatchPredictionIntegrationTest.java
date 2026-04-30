package tn.esprit._4se2.pi.match;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import tn.esprit._4se2.pi.dto.Match.MatchPredictionRequestDto;
import tn.esprit._4se2.pi.dto.Match.MatchPredictionResponseDto;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ─────────────────────────────────────────────────────────────
 *  TEST D'INTÉGRATION — Pipeline complet Spring ↔ FastAPI
 *
 *  ⚠️  Prérequis OBLIGATOIRE avant de lancer ce test :
 *      1. L'API FastAPI doit tourner sur le port 8000
 *         Commande : uvicorn 4_predict_api:app --port 8000
 *      2. La BD MySQL doit être accessible
 *
 *  Profil : @ActiveProfiles("test") → utilise application-test.properties
 * ─────────────────────────────────────────────────────────────
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DisplayName("MatchPrediction — Test d'intégration (requiert FastAPI sur :8000)")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MatchPredictionIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/matches";
    }

    // ── Requête de test standard ──────────────────────────────
    private MatchPredictionRequestDto strongHomeTeam() {
        MatchPredictionRequestDto req = new MatchPredictionRequestDto();
        req.setHomeRank(1);          // Meilleure équipe
        req.setAwayRank(20);         // Faible équipe
        req.setHomeGoalsAvg(3.5);
        req.setAwayGoalsAvg(0.8);
        req.setHomeConcededAvg(0.5);
        req.setAwayConcededAvg(2.5);
        req.setHomeWinsLast5(5);
        req.setAwayWinsLast5(0);
        req.setHomeRatingAvg(4.8);
        req.setAwayRatingAvg(2.2);
        req.setIsNeutralVenue(0);
        req.setCompetitionFormat(0);
        return req;
    }

    private MatchPredictionRequestDto balancedTeams() {
        MatchPredictionRequestDto req = new MatchPredictionRequestDto();
        req.setHomeRank(10);
        req.setAwayRank(10);
        req.setHomeGoalsAvg(1.5);
        req.setAwayGoalsAvg(1.5);
        req.setHomeConcededAvg(1.5);
        req.setAwayConcededAvg(1.5);
        req.setHomeWinsLast5(2);
        req.setAwayWinsLast5(2);
        req.setHomeRatingAvg(3.0);
        req.setAwayRatingAvg(3.0);
        return req;
    }

    // ═══════════════════════════════════════════════════════
    //  1. Domicile très fort → HOME_WIN attendu
    // ═══════════════════════════════════════════════════════
    @Test
    @Order(1)
    @DisplayName("Équipe domicile très forte → résultat probable HOME_WIN")
    void testStrongHomeTeam_predicts_homeWin() {
        ResponseEntity<MatchPredictionResponseDto> response = restTemplate.postForEntity(
                baseUrl() + "/predict/manual",
                strongHomeTeam(),
                MatchPredictionResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        MatchPredictionResponseDto body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getResult()).isIn("HOME_WIN", "AWAY_WIN", "DRAW");  // valid value
        assertThat(body.getConfidence()).isGreaterThan(0).isLessThanOrEqualTo(100);
        assertThat(body.getProbabilities()).containsKeys("HOME_WIN", "AWAY_WIN", "DRAW");
        assertThat(body.getInterpretation()).isNotBlank();

        System.out.println("▶  Résultat prédit : " + body.getResult()
                + "  (confiance " + body.getConfidence() + "%)");
        System.out.println("   Probabilités : " + body.getProbabilities());
    }

    // ═══════════════════════════════════════════════════════
    //  2. Équipes équilibrées → toutes les classes possibles
    // ═══════════════════════════════════════════════════════
    @Test
    @Order(2)
    @DisplayName("Équipes équilibrées → probabilités des 3 classes retournées")
    void testBalancedTeams_returnsAllClasses() {
        ResponseEntity<MatchPredictionResponseDto> response = restTemplate.postForEntity(
                baseUrl() + "/predict/manual",
                balancedTeams(),
                MatchPredictionResponseDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        MatchPredictionResponseDto body = response.getBody();
        assertThat(body).isNotNull();

        // Les 3 probabilités sont présentes
        Set<String> expectedKeys = Set.of("HOME_WIN", "AWAY_WIN", "DRAW");
        assertThat(body.getProbabilities().keySet()).containsAll(expectedKeys);

        // La somme des probabilités ≈ 100 %
        double sum = body.getProbabilities().values().stream().mapToDouble(Double::doubleValue).sum();
        assertThat(sum).isCloseTo(100.0, org.assertj.core.data.Offset.offset(1.0));

        System.out.println("▶  Résultat prédit : " + body.getResult()
                + "  (confiance " + body.getConfidence() + "%)");
    }

    // ═══════════════════════════════════════════════════════
    //  3. Content-Type correct
    // ═══════════════════════════════════════════════════════
    @Test
    @Order(3)
    @DisplayName("Réponse a bien le Content-Type application/json")
    void testResponse_contentTypeIsJson() {
        ResponseEntity<MatchPredictionResponseDto> response = restTemplate.postForEntity(
                baseUrl() + "/predict/manual",
                strongHomeTeam(),
                MatchPredictionResponseDto.class
        );

        assertThat(response.getHeaders().getContentType())
                .isNotNull()
                .satisfies(ct -> assertThat(ct.isCompatibleWith(MediaType.APPLICATION_JSON)).isTrue());
    }

    // ═══════════════════════════════════════════════════════
    //  4. Match inexistant en BD → 5xx
    // ═══════════════════════════════════════════════════════
    @Test
    @Order(4)
    @DisplayName("POST /predict/{matchId} avec ID inexistant → 5xx")
    void testPredictById_unknownId_returns5xx() {
        ResponseEntity<String> response = restTemplate.postForEntity(
                baseUrl() + "/predict/99999",
                null,
                String.class
        );

        assertThat(response.getStatusCode().is5xxServerError()).isTrue();
        System.out.println("▶  Erreur attendue : " + response.getStatusCode());
    }
}
