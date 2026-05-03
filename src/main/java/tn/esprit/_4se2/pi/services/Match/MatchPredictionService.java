package tn.esprit._4se2.pi.services.Match;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tn.esprit._4se2.pi.dto.Match.MatchPredictionRequestDto;
import tn.esprit._4se2.pi.dto.Match.MatchPredictionResponseDto;
import tn.esprit._4se2.pi.entities.Match;
import tn.esprit._4se2.pi.repositories.MatchRepository;

import java.util.List;

/**
 * Service Spring Boot qui appelle l'API FastAPI Python via RestTemplate.
 *
 * tableau blanc : Spring API (Rest Template) → BD MySQL
 *
 * Flux :
 *  1. Récupère les stats de l'équipe depuis la BD MySQL
 *  2. Construit un MatchPredictionRequestDto
 *  3. POST vers http://localhost:8000/predict (FastAPI)
 *  4. Retourne le résultat prédit au contrôleur
 */
@Service
public class MatchPredictionService {

    /** URL de l'API FastAPI — configurable dans application.properties */
    @Value("${ai.predict.url:http://localhost:8000/predict}")
    private String predictUrl;

    private final RestTemplate restTemplate;
    private final MatchRepository matchRepository;

    public MatchPredictionService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
        this.restTemplate    = new RestTemplate();
    }

    // ─────────────────────────────────────────────────────────
    /**
     * Prédit le résultat d'un match donné par son ID.
     * Les stats de forme sont calculées à partir des matchs passés en BD.
     */
    public MatchPredictionResponseDto predictMatch(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match introuvable : " + matchId));

        Long homeId = match.getHomeTeamId();
        Long awayId = match.getAwayTeamId();

        // ── Calcul de la forme sur les 5 derniers matchs ────
        int homeWins = countRecentWins(homeId, 5);
        int awayWins = countRecentWins(awayId, 5);

        // ── Calcul des moyennes depuis l'historique ──────────
        double homeGoalsAvg     = avgGoalsScored(homeId);
        double awayGoalsAvg     = avgGoalsScored(awayId);
        double homeConcededAvg  = avgGoalsConceded(homeId);
        double awayConcededAvg  = avgGoalsConceded(awayId);

        // ── Construction du DTO ──────────────────────────────
        MatchPredictionRequestDto req = new MatchPredictionRequestDto();
        req.setHomeRank(10);                    // TODO: récupérer depuis classement compétition
        req.setAwayRank(10);
        req.setHomeGoalsAvg(homeGoalsAvg);
        req.setAwayGoalsAvg(awayGoalsAvg);
        req.setHomeConcededAvg(homeConcededAvg);
        req.setAwayConcededAvg(awayConcededAvg);
        req.setHomeWinsLast5(homeWins);
        req.setAwayWinsLast5(awayWins);
        req.setHomeRatingAvg(3.5);              // TODO: récupérer depuis PlayerStats
        req.setAwayRatingAvg(3.5);
        req.setIsNeutralVenue(0);

        // ── Appel REST vers FastAPI ──────────────────────────
        return restTemplate.postForObject(predictUrl, req, MatchPredictionResponseDto.class);
    }

    /**
     * Prédit à partir d'un DTO fourni directement (utile pour les tests frontend).
     */
    public MatchPredictionResponseDto predictFromDto(MatchPredictionRequestDto req) {
        return restTemplate.postForObject(predictUrl, req, MatchPredictionResponseDto.class);
    }

    // ─── Helpers statistiques ────────────────────────────────

    private int countRecentWins(Long teamId, int last) {
        List<Match> finished = matchRepository.findFinishedByTeam(teamId);
        int count = 0;
        int checked = 0;
        for (int i = finished.size() - 1; i >= 0 && checked < last; i--, checked++) {
            Match m = finished.get(i);
            boolean homeWin = m.getHomeTeamId().equals(teamId) && m.getHomeScore() > m.getAwayScore();
            boolean awayWin = m.getAwayTeamId().equals(teamId) && m.getAwayScore() > m.getHomeScore();
            if (homeWin || awayWin) count++;
        }
        return count;
    }

    private double avgGoalsScored(Long teamId) {
        List<Match> matches = matchRepository.findFinishedByTeam(teamId);
        if (matches.isEmpty()) return 1.0;
        double total = matches.stream().mapToDouble(m ->
            m.getHomeTeamId().equals(teamId) ? m.getHomeScore() : m.getAwayScore()
        ).sum();
        return Math.round((total / matches.size()) * 100.0) / 100.0;
    }

    private double avgGoalsConceded(Long teamId) {
        List<Match> matches = matchRepository.findFinishedByTeam(teamId);
        if (matches.isEmpty()) return 1.0;
        double total = matches.stream().mapToDouble(m ->
            m.getHomeTeamId().equals(teamId) ? m.getAwayScore() : m.getHomeScore()
        ).sum();
        return Math.round((total / matches.size()) * 100.0) / 100.0;
    }
}
