package tn.esprit._4se2.pi.restcontrollers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.dto.Match.MatchPredictionRequestDto;
import tn.esprit._4se2.pi.dto.Match.MatchPredictionResponseDto;
import tn.esprit._4se2.pi.services.Match.MatchPredictionService;

/**
 * Contrôleur REST — Prédiction IA des matchs.
 * Flux : Angular → Spring (8082) → FastAPI Python (8000) → sklearn → réponse
 */
@RestController
@RequestMapping("/api/matches")
@CrossOrigin(origins = "*")
@Tag(
    name = "🤖 IA Prédiction",
    description = """
        Endpoints de **prédiction du résultat** des matchs par Intelligence Artificielle.
        
        Le modèle (Random Forest / Gradient Boosting) est entraîné sur 1000 matchs simulés
        et prédit : **HOME_WIN**, **AWAY_WIN** ou **DRAW** avec un pourcentage de confiance.
        
        ⚠️ **Prérequis** : l'API Python FastAPI doit tourner sur `http://localhost:8000`
        
        Commande : `uvicorn 4_predict_api:app --port 8000 --reload`
        """
)
public class MatchPredictionController {

    private final MatchPredictionService predictionService;

    public MatchPredictionController(MatchPredictionService predictionService) {
        this.predictionService = predictionService;
    }

    // ═══════════════════════════════════════════════════════
    //  1. Prédiction depuis la BD (par matchId)
    // ═══════════════════════════════════════════════════════
    @Operation(
        summary     = "🔮 Prédire depuis la BD",
        description = """
            Récupère un match existant en base MySQL par son ID,
            calcule automatiquement les stats des équipes (forme, buts moyens, etc.)
            et appelle le modèle IA Python pour prédire le résultat.
            
            **Réponse possible** : `HOME_WIN` | `AWAY_WIN` | `DRAW`
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description  = "Prédiction réussie",
            content      = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema    = @Schema(implementation = MatchPredictionResponseDto.class),
                examples  = @ExampleObject(name = "Exemple HOME_WIN", value = """
                    {
                      "result": "HOME_WIN",
                      "confidence": 78.5,
                      "probabilities": {
                        "HOME_WIN": 78.5,
                        "AWAY_WIN": 14.2,
                        "DRAW": 7.3
                      },
                      "interpretation": "L'équipe domicile devrait gagner avec 78% de confiance."
                    }
                    """)
            )
        ),
        @ApiResponse(responseCode = "500",
            description = "Match introuvable en BD ou API Python non démarrée")
    })
    @PostMapping("/predict/{matchId}")
    public ResponseEntity<MatchPredictionResponseDto> predictById(
            @Parameter(
                description = "ID du match en base MySQL (doit être SCHEDULED ou LIVE)",
                example     = "1",
                required    = true
            )
            @PathVariable Long matchId) {
        MatchPredictionResponseDto response = predictionService.predictMatch(matchId);
        return ResponseEntity.ok(response);
    }

    // ═══════════════════════════════════════════════════════
    //  2. Prédiction manuelle (via formulaire Swagger)
    // ═══════════════════════════════════════════════════════
    @Operation(
        summary     = "🎯 Prédire manuellement (test Swagger)",
        description = """
            Permet de tester la prédiction directement depuis Swagger UI
            en saisissant les stats des deux équipes manuellement.
            
            Ne nécessite **aucune donnée en BD** — idéal pour les démonstrations.
            
            ### Valeurs de test suggérées
            | Scénario | home_rank | away_rank | home_wins_last5 | away_wins_last5 |
            |---|---|---|---|---|
            | Domicile fort | 1 | 20 | 5 | 0 |
            | Équipes égales | 10 | 10 | 2 | 2 |
            | Extérieur fort | 25 | 2 | 0 | 5 |
            """
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description  = "Prédiction réussie",
            content      = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema    = @Schema(implementation = MatchPredictionResponseDto.class),
                examples  = {
                    @ExampleObject(name = "🏠 Domicile fort → HOME_WIN", value = """
                        {
                          "result": "HOME_WIN",
                          "confidence": 82.3,
                          "probabilities": { "HOME_WIN": 82.3, "AWAY_WIN": 10.1, "DRAW": 7.6 },
                          "interpretation": "L'équipe domicile devrait gagner avec 82% de confiance."
                        }
                        """),
                    @ExampleObject(name = "🔄 Équipes égales → DRAW", value = """
                        {
                          "result": "DRAW",
                          "confidence": 45.1,
                          "probabilities": { "HOME_WIN": 32.4, "AWAY_WIN": 22.5, "DRAW": 45.1 },
                          "interpretation": "Le match devrait se terminer sur un match nul."
                        }
                        """)
                }
            )
        ),
        @ApiResponse(responseCode = "500",
            description = "API Python FastAPI non démarrée sur le port 8000")
    })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Statistiques des deux équipes pour la prédiction",
        required    = true,
        content     = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            examples  = {
                @ExampleObject(name = "🏠 Test — Domicile très fort", value = """
                    {
                      "homeRank": 1,
                      "awayRank": 20,
                      "homeGoalsAvg": 3.5,
                      "awayGoalsAvg": 0.8,
                      "homeConcededAvg": 0.5,
                      "awayConcededAvg": 2.5,
                      "homeWinsLast5": 5,
                      "awayWinsLast5": 0,
                      "homeRatingAvg": 4.8,
                      "awayRatingAvg": 2.2,
                      "isNeutralVenue": 0,
                      "competitionFormat": 0
                    }
                    """),
                @ExampleObject(name = "⚖️ Test — Équipes équilibrées", value = """
                    {
                      "homeRank": 10,
                      "awayRank": 10,
                      "homeGoalsAvg": 1.5,
                      "awayGoalsAvg": 1.5,
                      "homeConcededAvg": 1.5,
                      "awayConcededAvg": 1.5,
                      "homeWinsLast5": 2,
                      "awayWinsLast5": 2,
                      "homeRatingAvg": 3.0,
                      "awayRatingAvg": 3.0,
                      "isNeutralVenue": 1,
                      "competitionFormat": 1
                    }
                    """),
                @ExampleObject(name = "✈️ Test — Extérieur très fort", value = """
                    {
                      "homeRank": 25,
                      "awayRank": 2,
                      "homeGoalsAvg": 0.7,
                      "awayGoalsAvg": 3.2,
                      "homeConcededAvg": 2.8,
                      "awayConcededAvg": 0.6,
                      "homeWinsLast5": 0,
                      "awayWinsLast5": 5,
                      "homeRatingAvg": 2.1,
                      "awayRatingAvg": 4.7,
                      "isNeutralVenue": 0,
                      "competitionFormat": 2
                    }
                    """)
            }
        )
    )
    @PostMapping("/predict/manual")
    public ResponseEntity<MatchPredictionResponseDto> predictManual(
            @RequestBody MatchPredictionRequestDto request) {
        MatchPredictionResponseDto response = predictionService.predictFromDto(request);
        return ResponseEntity.ok(response);
    }
}
