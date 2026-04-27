package tn.esprit._4se2.pi.services.MatchEvent;

import org.springframework.stereotype.Service;
import tn.esprit._4se2.pi.Enum.MatchEventType;
import tn.esprit._4se2.pi.dto.MatchEvent.PlayerStatsDto;
import tn.esprit._4se2.pi.dto.MatchEvent.TimelineEventDto;
import tn.esprit._4se2.pi.entities.Match;
import tn.esprit._4se2.pi.entities.MatchEvent;
import tn.esprit._4se2.pi.entities.Team;
import tn.esprit._4se2.pi.repositories.MatchEventRepository;
import tn.esprit._4se2.pi.repositories.MatchRepository;
import tn.esprit._4se2.pi.repositories.TeamRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchEventService {
    private final MatchEventRepository matchEventRepository;
    private final MatchRepository matchRepository;
    private final TeamRepository teamRepository;

    public MatchEventService(MatchEventRepository matchEventRepository,
                             MatchRepository matchRepository,
                             TeamRepository teamRepository) {
        this.matchEventRepository = matchEventRepository;
        this.matchRepository = matchRepository;
        this.teamRepository = teamRepository;
    }

    /**
     * Log event with business validation:
     * - A player with RED_CARD cannot score GOAL after that minute
     */
    public MatchEvent logEvent(MatchEvent event) {
        if (event.getPoints() == null) event.setPoints(1);

        if (event.getPlayerId() != null && (MatchEventType.SCORE.equals(event.getEventType()) || MatchEventType.GOAL.equals(event.getEventType()))) {
            List<MatchEvent> playerEvents = matchEventRepository.findByMatchIdAndPlayerId(
                    event.getMatchId(), event.getPlayerId());
            boolean ejected = playerEvents.stream()
                    .anyMatch(e -> (MatchEventType.EJECTION.equals(e.getEventType()) || MatchEventType.RED_CARD.equals(e.getEventType()))
                            && e.getMinute() <= event.getMinute());
            if (ejected) {
                throw new IllegalStateException(
                        "Joueur #" + event.getPlayerId() + " a été expulsé — il ne peut pas marquer après son expulsion.");
            }
        }
        MatchEvent saved = matchEventRepository.save(event);
        
        if (MatchEventType.SCORE.equals(event.getEventType()) || MatchEventType.GOAL.equals(event.getEventType())) {
            Match match = matchRepository.findById(event.getMatchId()).orElse(null);
            if (match != null) {
                if (event.getTeamId() != null && event.getTeamId().equals(match.getHomeTeamId())) {
                    match.setHomeScore((match.getHomeScore() == null ? 0 : match.getHomeScore()) + event.getPoints());
                } else if (event.getTeamId() != null && event.getTeamId().equals(match.getAwayTeamId())) {
                    match.setAwayScore((match.getAwayScore() == null ? 0 : match.getAwayScore()) + event.getPoints());
                }
                matchRepository.save(match);
            }
        }
        return saved;
    }

    public List<MatchEvent> getByMatchId(Long matchId) {
        return matchEventRepository.findByMatchId(matchId);
    }

    public void delete(Long id) { matchEventRepository.deleteById(id); }

    // ─── MÉTIER 1 — Event Timeline Engine ────────────────────────────────────

    public List<TimelineEventDto> getTimeline(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));

        List<MatchEvent> events = matchEventRepository.findByMatchIdOrderByMinuteAsc(matchId);

        Long homeId = match.getHomeTeamId(), awayId = match.getAwayTeamId();
        Map<Long, Team> teamsMap = new HashMap<>();
        teamRepository.findAllById(Arrays.asList(homeId, awayId)).forEach(t -> teamsMap.put(t.getId(), t));

        // Track expulsions: playerId -> minute of red card
        Map<Long, Integer> ejectedPlayers = new HashMap<>();

        int homeScore = 0, awayScore = 0;
        List<TimelineEventDto> timeline = new ArrayList<>();

        for (MatchEvent ev : events) {
            // Update score snapshot
            if (MatchEventType.SCORE.equals(ev.getEventType()) || MatchEventType.GOAL.equals(ev.getEventType())) {
                int pts = ev.getPoints() != null ? ev.getPoints() : 1;
                if (homeId.equals(ev.getTeamId())) homeScore += pts;
                else if (awayId.equals(ev.getTeamId())) awayScore += pts;
            }
            if ((MatchEventType.EJECTION.equals(ev.getEventType()) || MatchEventType.RED_CARD.equals(ev.getEventType())) && ev.getPlayerId() != null) {
                ejectedPlayers.put(ev.getPlayerId(), ev.getMinute());
            }

            TimelineEventDto dto = new TimelineEventDto();
            dto.setId(ev.getId());
            dto.setMatchId(ev.getMatchId());
            dto.setType(ev.getEventType());
            dto.setMinute(ev.getMinute());
            dto.setTeamId(ev.getTeamId());
            dto.setPlayerId(ev.getPlayerId());
            dto.setDescription(ev.getDescription());
            dto.setPoints(ev.getPoints() != null ? ev.getPoints() : 1);
            dto.setHomeScoreSnapshot(homeScore);
            dto.setAwayScoreSnapshot(awayScore);

            // Team name
            Team team = teamsMap.get(ev.getTeamId());
            dto.setTeamName(team != null ? team.getName() : "Équipe " + ev.getTeamId());

            // Momentum: dominant team in last 15 min
            int currentMin = ev.getMinute();
            long homeRecent = events.stream()
                    .filter(e -> (MatchEventType.SCORE.equals(e.getEventType()) || MatchEventType.GOAL.equals(e.getEventType())) && homeId.equals(e.getTeamId())
                            && e.getMinute() >= currentMin - 15 && e.getMinute() <= currentMin).count();
            long awayRecent = events.stream()
                    .filter(e -> (MatchEventType.SCORE.equals(e.getEventType()) || MatchEventType.GOAL.equals(e.getEventType())) && awayId.equals(e.getTeamId())
                            && e.getMinute() >= currentMin - 15 && e.getMinute() <= currentMin).count();
            if (homeRecent > awayRecent) dto.setMomentum("HOME");
            else if (awayRecent > homeRecent) dto.setMomentum("AWAY");
            else dto.setMomentum("NEUTRAL");

            // Anomaly detection
            List<String> anomalies = new ArrayList<>();
            if ((MatchEventType.SCORE.equals(ev.getEventType()) || MatchEventType.GOAL.equals(ev.getEventType())) && ev.getPlayerId() != null) {
                Integer ejectedAt = ejectedPlayers.get(ev.getPlayerId());
                if (ejectedAt != null && ejectedAt < ev.getMinute()) {
                    anomalies.add("⚠️ Score marqué par un joueur expulsé (minute " + ejectedAt + ")");
                }
            }
            if ((MatchEventType.EJECTION.equals(ev.getEventType()) || MatchEventType.RED_CARD.equals(ev.getEventType())) && ev.getPlayerId() != null) {
                long previousReds = events.stream()
                        .filter(e -> (MatchEventType.EJECTION.equals(e.getEventType()) || MatchEventType.RED_CARD.equals(e.getEventType()))
                                && ev.getPlayerId().equals(e.getPlayerId())
                                && e.getMinute() < ev.getMinute()).count();
                if (previousReds >= 1) {
                    anomalies.add("⚠️ Deuxième expulsion pour le même joueur #" + ev.getPlayerId());
                }
            }
            // Score after minute 90 without extra time context (Football specific, but kept generic)
            if ((MatchEventType.SCORE.equals(ev.getEventType()) || MatchEventType.GOAL.equals(ev.getEventType())) && ev.getMinute() > 90) {
                anomalies.add("ℹ️ Score en temps additionnel (minute " + ev.getMinute() + ")");
            }

            dto.setAnomalies(anomalies);
            timeline.add(dto);
        }

        return timeline;
    }

    // ─── MÉTIER 2 — Player Stats Aggregator ──────────────────────────────────

    public PlayerStatsDto getPlayerStats(Long matchId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new RuntimeException("Match not found"));
        List<MatchEvent> events = matchEventRepository.findByMatchIdOrderByMinuteAsc(matchId);

        Long homeId = match.getHomeTeamId(), awayId = match.getAwayTeamId();
        Map<Long, Team> teamsMap = new HashMap<>();
        teamRepository.findAllById(Arrays.asList(homeId, awayId)).forEach(t -> teamsMap.put(t.getId(), t));

        // Aggregate per player: playerId -> stats array
        // [goals, assists, yellowCards, redCards, fouls, substitutions]
        Map<Long, int[]> playerAgg = new LinkedHashMap<>();
        Map<Long, Long> playerTeam = new HashMap<>();

        for (MatchEvent ev : events) {
            if (ev.getPlayerId() == null) continue;
            playerAgg.putIfAbsent(ev.getPlayerId(), new int[]{0, 0, 0, 0, 0, 0});
            playerTeam.put(ev.getPlayerId(), ev.getTeamId());
            int[] s = playerAgg.get(ev.getPlayerId());
            switch (ev.getEventType()) {
                case SCORE:
                case GOAL:
                    s[0] += (ev.getPoints() != null ? ev.getPoints() : 1);
                    break;
                case ASSIST: s[1]++; break;
                case WARNING:
                case YELLOW_CARD:
                    s[2]++; break;
                case EJECTION:
                case RED_CARD:
                    s[3]++; break;
                case FOUL: s[4]++; break;
                case SUBSTITUTION: s[5]++; break;
                default: break;
            }
        }

        // Build entries
        List<PlayerStatsDto.PlayerEntry> allPlayers = new ArrayList<>();
        for (Map.Entry<Long, int[]> e : playerAgg.entrySet()) {
            Long pid = e.getKey();
            int[] s = e.getValue();
            Long tid = playerTeam.get(pid);
            Team team = teamsMap.get(tid);

            // rating /5: goals*3 + assists*1.5 - fouls*0.5 - yellows*1 - reds*2
            double raw = s[0] * 3.0 + s[1] * 1.5 - s[4] * 0.5 - s[2] * 1.0 - s[3] * 2.0;
            double rating = Math.max(0, Math.min(5, raw));
            rating = Math.round(rating * 10.0) / 10.0;
            int stars = (int) Math.ceil(rating);

            PlayerStatsDto.PlayerEntry entry = new PlayerStatsDto.PlayerEntry();
            entry.setPlayerId(pid);
            entry.setPlayerName("Joueur #" + pid);
            entry.setTeamId(tid);
            entry.setTeamName(team != null ? team.getName() : "Team " + tid);
            entry.setGoals(s[0]);
            entry.setAssists(s[1]);
            entry.setYellowCards(s[2]);
            entry.setRedCards(s[3]);
            entry.setFouls(s[4]);
            entry.setSubstitutions(s[5]);
            entry.setRating(rating);
            entry.setStars(stars);
            entry.setEjected(s[3] > 0);
            allPlayers.add(entry);
        }

        PlayerStatsDto dto = new PlayerStatsDto();
        dto.setAllPlayers(allPlayers);

        // Rankings
        dto.setTopScorers(allPlayers.stream()
                .filter(p -> p.getGoals() > 0)
                .sorted((a, b) -> b.getGoals() - a.getGoals())
                .limit(10).collect(Collectors.toList()));

        dto.setTopAssists(allPlayers.stream()
                .filter(p -> p.getAssists() > 0)
                .sorted((a, b) -> Double.compare(b.getAssists(), a.getAssists()))
                .limit(10).collect(Collectors.toList()));

        dto.setBestRated(allPlayers.stream()
                .sorted((a, b) -> Double.compare(b.getRating(), a.getRating()))
                .limit(10).collect(Collectors.toList()));

        dto.setMostDisciplined(allPlayers.stream()
                .filter(p -> p.getYellowCards() == 0 && p.getRedCards() == 0)
                .sorted((a, b) -> b.getGoals() - a.getGoals())
                .limit(10).collect(Collectors.toList()));

        return dto;
    }
}
