package tn.esprit._4se2.pi.services.Competition;

import org.springframework.stereotype.Service;
import tn.esprit._4se2.pi.Enum.*;
import tn.esprit._4se2.pi.dto.Competition.*;
import tn.esprit._4se2.pi.entities.*;
import tn.esprit._4se2.pi.repositories.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CompetitionService {
    private final CompetitionRepository competitionRepository;
    private final RegistrationRepository registrationRepository;
    private final MatchRepository matchRepository;
    private final MatchEventRepository matchEventRepository;
    private final TeamRepository teamRepository;

    public CompetitionService(CompetitionRepository competitionRepository,
                              RegistrationRepository registrationRepository,
                              MatchRepository matchRepository,
                              MatchEventRepository matchEventRepository,
                              TeamRepository teamRepository) {
        this.competitionRepository = competitionRepository;
        this.registrationRepository = registrationRepository;
        this.matchRepository = matchRepository;
        this.matchEventRepository = matchEventRepository;
        this.teamRepository = teamRepository;
    }

    public Competition create(Competition c) { return competitionRepository.save(c); }
    public Competition update(Long id, Competition c) { c.setId(id); return competitionRepository.save(c); }
    public List<Competition> getAll() { return competitionRepository.findAll(); }
    public Competition getById(Long id) { return competitionRepository.findById(id).orElseThrow(() -> new RuntimeException("Competition not found")); }
    public void delete(Long id) { competitionRepository.deleteById(id); }

    // ─── MÉTIER 1 — Seeding Engine ────────────────────────────────────────────

    public List<SeedingResultDto> computeSeeding(Long competitionId, String strategy, SeedingRequestDto manualSeeds) {
        // Teams from registrations
        List<Registration> regs = registrationRepository.findByCompetitionIdAndStatus(competitionId, RegistrationStatus.CONFIRMED);
        if (regs.isEmpty()) regs = registrationRepository.findByCompetitionId(competitionId);
        Set<Long> teamIds = regs.stream().map(Registration::getTeamId).collect(Collectors.toSet());

        // Teams from matches (for direct liaison)
        List<Match> allMatches = matchRepository.findByCompetitionId(competitionId);
        for (Match m : allMatches) {
            teamIds.add(m.getHomeTeamId());
            teamIds.add(m.getAwayTeamId());
        }

        if (teamIds.isEmpty()) throw new RuntimeException("Aucune équipe (inscrite ou en match) pour cette compétition.");

        List<Match> finished = allMatches.stream()
                .filter(m -> MatchStatus.FINISHED.equals(m.getStatus())).toList();

        Map<Long, int[]> stats = new HashMap<>();
        for (Long tid : teamIds) stats.put(tid, new int[]{0, 0, 0, 0, 0}); // wins,losses,draws,gf,ga

        for (Match m : finished) {
            // Recalculate score from events
            List<MatchEvent> matchEvents = matchEventRepository.findByMatchIdOrderByMinuteAsc(m.getId());
            int hs = (int) matchEvents.stream().filter(e -> (MatchEventType.SCORE.equals(e.getEventType()) || MatchEventType.GOAL.equals(e.getEventType())) && e.getTeamId().equals(m.getHomeTeamId())).count();
            int as = (int) matchEvents.stream().filter(e -> (MatchEventType.SCORE.equals(e.getEventType()) || MatchEventType.GOAL.equals(e.getEventType())) && e.getTeamId().equals(m.getAwayTeamId())).count();

            if (stats.containsKey(m.getHomeTeamId())) {
                stats.get(m.getHomeTeamId())[3] += hs; stats.get(m.getHomeTeamId())[4] += as;
                if (hs > as) stats.get(m.getHomeTeamId())[0]++;
                else if (hs < as) stats.get(m.getHomeTeamId())[1]++;
                else stats.get(m.getHomeTeamId())[2]++;
            }
            if (stats.containsKey(m.getAwayTeamId())) {
                stats.get(m.getAwayTeamId())[3] += as; stats.get(m.getAwayTeamId())[4] += hs;
                if (as > hs) stats.get(m.getAwayTeamId())[0]++;
                else if (as < hs) stats.get(m.getAwayTeamId())[1]++;
                else stats.get(m.getAwayTeamId())[2]++;
            }
        }

        Map<Long, Team> teamsMap = teamRepository.findAllById(teamIds).stream()
                .collect(Collectors.toMap(Team::getId, t -> t));

        List<SeedingResultDto> results = new ArrayList<>();
        for (Long tid : teamIds) {
            int[] s = stats.getOrDefault(tid, new int[]{0,0,0,0,0});
            int played = s[0] + s[1] + s[2];
            double winRate = played > 0 ? (double) s[0] / played : 0.0;
            double elo = 1000 + s[0] * 30 - s[1] * 20 + (s[3] - s[4]) * 5;
            Team t = teamsMap.get(tid);
            results.add(new SeedingResultDto(tid, t != null ? t.getName() : "Team " + tid,
                    0, Math.round(winRate * 1000.0) / 1000.0, Math.round(elo * 10.0) / 10.0,
                    s[0], s[1], s[2], played));
        }

        switch (strategy.toUpperCase()) {
            case "ELO":
                results.sort((a, b) -> {
                    int cmp = Double.compare(b.getEloScore(), a.getEloScore());
                    if (cmp == 0) cmp = Double.compare(b.getWinRate(), a.getWinRate());
                    return cmp;
                });
                break;
            case "MANUAL":
                if (manualSeeds != null && manualSeeds.getSeeds() != null) {
                    Map<Long, Integer> mm = manualSeeds.getSeeds().stream()
                            .collect(Collectors.toMap(SeedingRequestDto.ManualSeedEntry::getTeamId, SeedingRequestDto.ManualSeedEntry::getSeed));
                    results.sort(Comparator.comparingInt(r -> mm.getOrDefault(r.getTeamId(), 999)));
                }
                break;
            default: // WIN_RATE
                results.sort((a, b) -> {
                    int cmp = Double.compare(b.getWinRate(), a.getWinRate());
                    if (cmp == 0) cmp = Double.compare(b.getEloScore(), a.getEloScore());
                    return cmp;
                });
                break;
        }

        for (int i = 0; i < results.size(); i++) results.get(i).setSeed(i + 1);
        reorganiseBracket(competitionId, results);
        return results;
    }

    private void reorganiseBracket(Long competitionId, List<SeedingResultDto> seeds) {
        List<Match> scheduled = matchRepository.findByCompetitionIdAndStatus(competitionId, MatchStatus.SCHEDULED);
        if (scheduled.isEmpty()) return;
        int n = seeds.size(), pairs = Math.min(scheduled.size(), n / 2);
        for (int i = 0; i < pairs; i++) {
            Match m = scheduled.get(i);
            m.setHomeTeamId(seeds.get(i).getTeamId());
            m.setAwayTeamId(seeds.get(n - 1 - i).getTeamId());
            matchRepository.save(m);
        }
    }

    // ─── MÉTIER 2 — Competition Analytics ────────────────────────────────────

    public CompetitionAnalyticsDto getAnalytics(Long competitionId) {
        CompetitionAnalyticsDto dto = new CompetitionAnalyticsDto();
        List<Match> allMatches = matchRepository.findByCompetitionId(competitionId);
        List<Match> finished = allMatches.stream()
                .filter(m -> MatchStatus.FINISHED.equals(m.getStatus())).toList();

        dto.setTotalMatches(allMatches.size());
        dto.setFinishedMatches(finished.size());

        if (finished.isEmpty()) {
            dto.setTopScorers(Collections.emptyList());
            dto.setCardsSummary(Collections.emptyList());
            return dto;
        }

        List<Long> matchIds = finished.stream().map(Match::getId).toList();
        List<MatchEvent> events = matchEventRepository.findByMatchIdIn(matchIds);

        Set<Long> teamIdSet = new HashSet<>();
        for (Match m : finished) { teamIdSet.add(m.getHomeTeamId()); teamIdSet.add(m.getAwayTeamId()); }
        Map<Long, Team> teamsMap = teamRepository.findAllById(new ArrayList<>(teamIdSet))
                .stream().collect(Collectors.toMap(Team::getId, t -> t));

        Map<Long, Integer> goalsScored = new HashMap<>(), goalsConceded = new HashMap<>();
        Map<Long, Integer> yellowCards = new HashMap<>(), redCards = new HashMap<>();
        for (Long tid : teamIdSet) { goalsScored.put(tid, 0); goalsConceded.put(tid, 0); yellowCards.put(tid, 0); redCards.put(tid, 0); }

        for (Match m : finished) {
            int hs = m.getHomeScore() != null ? m.getHomeScore() : 0;
            int as = m.getAwayScore() != null ? m.getAwayScore() : 0;
            goalsScored.merge(m.getHomeTeamId(), hs, Integer::sum);
            goalsScored.merge(m.getAwayTeamId(), as, Integer::sum);
            goalsConceded.merge(m.getHomeTeamId(), as, Integer::sum);
            goalsConceded.merge(m.getAwayTeamId(), hs, Integer::sum);
        }
        for (MatchEvent ev : events) {
            if (ev.getTeamId() == null) continue;
            if (MatchEventType.WARNING.equals(ev.getEventType()) || MatchEventType.YELLOW_CARD.equals(ev.getEventType())) 
                yellowCards.merge(ev.getTeamId(), 1, Integer::sum);
            if (MatchEventType.EJECTION.equals(ev.getEventType()) || MatchEventType.RED_CARD.equals(ev.getEventType())) 
                redCards.merge(ev.getTeamId(), 1, Integer::sum);
        }

        // Best attack
        goalsScored.entrySet().stream().max(Map.Entry.comparingByValue()).ifPresent(e -> {
            Team t = teamsMap.get(e.getKey());
            dto.setBestAttack(new CompetitionAnalyticsDto.TeamStatEntry(e.getKey(), t != null ? t.getName() : "Team " + e.getKey(), e.getValue()));
        });
        // Best defense
        goalsConceded.entrySet().stream().min(Map.Entry.comparingByValue()).ifPresent(e -> {
            Team t = teamsMap.get(e.getKey());
            dto.setBestDefense(new CompetitionAnalyticsDto.TeamStatEntry(e.getKey(), t != null ? t.getName() : "Team " + e.getKey(), e.getValue()));
        });

        // Top scorers
        Map<Long, int[]> scorerStats = new HashMap<>();
        for (MatchEvent ev : events) {
            if (ev.getPlayerId() == null) continue;
            scorerStats.putIfAbsent(ev.getPlayerId(), new int[]{0, 0});
            if (MatchEventType.SCORE.equals(ev.getEventType()) || MatchEventType.GOAL.equals(ev.getEventType())) 
                scorerStats.get(ev.getPlayerId())[0] += (ev.getPoints() != null ? ev.getPoints() : 1);
            if (MatchEventType.ASSIST.equals(ev.getEventType())) 
                scorerStats.get(ev.getPlayerId())[1]++;
        }
        dto.setTopScorers(scorerStats.entrySet().stream()
                .sorted((a, b) -> b.getValue()[0] - a.getValue()[0]).limit(10)
                .map(e -> new CompetitionAnalyticsDto.PlayerGoalEntry(e.getKey(), "Joueur #" + e.getKey(), e.getValue()[0], e.getValue()[1]))
                .collect(Collectors.toList()));

        // Totals
        dto.setTotalGoals(goalsScored.values().stream().mapToInt(Integer::intValue).sum() / 2 + 1);
        dto.setTotalWarnings(yellowCards.values().stream().mapToInt(Integer::intValue).sum());
        dto.setTotalEjections(redCards.values().stream().mapToInt(Integer::intValue).sum());

        // Cards summary
        dto.setCardsSummary(teamIdSet.stream().map(tid -> {
            Team t = teamsMap.get(tid);
            return new CompetitionAnalyticsDto.TeamCardSummary(tid, t != null ? t.getName() : "Team " + tid, yellowCards.getOrDefault(tid, 0), redCards.getOrDefault(tid, 0));
        }).sorted((a, b) -> (b.getWarnings() + b.getEjections() * 3) - (a.getWarnings() + a.getEjections() * 3))
                .toList());
        List<Long> byStrength = goalsScored.entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .map(Map.Entry::getKey).toList();
        Map<Long, Integer> seedMap = new HashMap<>();
        for (int i = 0; i < byStrength.size(); i++) seedMap.put(byStrength.get(i), i + 1);
        int upsets = computeUpsets(finished, seedMap);
        dto.setTotalUpsets(upsets);
        return dto;
    }

    private int computeUpsets(List<Match> finished, Map<Long, Integer> seedMap) {
        int upsets = 0;
        for (Match m : finished) {
            int hs = m.getHomeScore() != null ? m.getHomeScore() : 0, as = m.getAwayScore() != null ? m.getAwayScore() : 0;
            int homeSeed = seedMap.getOrDefault(m.getHomeTeamId(), 999), awaySeed = seedMap.getOrDefault(m.getAwayTeamId(), 999);
            if (hs > as && homeSeed > awaySeed) upsets++;
            if (as > hs && awaySeed > homeSeed) upsets++;
        }
        return upsets;
    }
}
