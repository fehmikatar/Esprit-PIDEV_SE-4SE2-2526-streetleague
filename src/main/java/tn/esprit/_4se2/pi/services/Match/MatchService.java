package tn.esprit._4se2.pi.services.Match;

import org.springframework.stereotype.Service;
import tn.esprit._4se2.pi.Enum.MatchEventType;
import tn.esprit._4se2.pi.Enum.MatchStatus;
import tn.esprit._4se2.pi.dto.Match.MatchRatingDto;
import tn.esprit._4se2.pi.dto.Match.ScheduleRequestDto;
import tn.esprit._4se2.pi.dto.Match.ScheduleResultDto;
import tn.esprit._4se2.pi.entities.Match;
import tn.esprit._4se2.pi.entities.MatchEvent;
import tn.esprit._4se2.pi.entities.Team;
import tn.esprit._4se2.pi.repositories.MatchEventRepository;
import tn.esprit._4se2.pi.repositories.MatchRepository;
import tn.esprit._4se2.pi.repositories.TeamRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchService {
    private final MatchRepository matchRepository;
    private final MatchEventRepository matchEventRepository;
    private final TeamRepository teamRepository;

    public MatchService(MatchRepository matchRepository,
                        MatchEventRepository matchEventRepository,
                        TeamRepository teamRepository) {
        this.matchRepository = matchRepository;
        this.matchEventRepository = matchEventRepository;
        this.teamRepository = teamRepository;
    }

    public Match create(Match match) { return matchRepository.save(match); }

    public List<Match> getAll(Long competitionId) {
        if (competitionId != null) return matchRepository.findByCompetitionId(competitionId);
        return matchRepository.findAll();
    }

    public Match getById(Long id) {
        return matchRepository.findById(id).orElseThrow(() -> new RuntimeException("Match not found"));
    }

    public void delete(Long id) { matchRepository.deleteById(id); }

    public Match update(Long id, Match match) {
        match.setId(id);
        return matchRepository.save(match);
    }

    public Match updateLiveScore(Long matchId, Integer homeScore, Integer awayScore) {
        Match match = getById(matchId);
        match.setHomeScore(homeScore);
        match.setAwayScore(awayScore);
        return matchRepository.save(match);
    }

    public Match finishMatch(Long matchId) {
        Match match = getById(matchId);
        match.setStatus(MatchStatus.FINISHED);
        return matchRepository.save(match);
    }

    public Match startMatch(Long matchId) {
        Match match = getById(matchId);
        match.setStatus(MatchStatus.LIVE);
        match.setStartedAt(LocalDateTime.now());
        return matchRepository.save(match);
    }

    // ─── MÉTIER 1 — Performance Rating Engine ────────────────────────────────

    public MatchRatingDto computeRating(Long matchId) {
        Match match = getById(matchId);
        List<MatchEvent> events = matchEventRepository.findByMatchIdOrderByMinuteAsc(matchId);

        Long homeId = match.getHomeTeamId(), awayId = match.getAwayTeamId();
        int hs = match.getHomeScore() != null ? match.getHomeScore() : 0;
        int as = match.getAwayScore() != null ? match.getAwayScore() : 0;
        int maxGoals = Math.max(hs, as);

        Map<Long, Team> teamsMap = new HashMap<>();
        teamRepository.findAllById(Arrays.asList(homeId, awayId))
                .forEach(t -> teamsMap.put(t.getId(), t));

        MatchRatingDto dto = new MatchRatingDto();
        dto.setHomeRating(buildTeamRating(homeId, teamsMap.get(homeId), hs, as, maxGoals, events, match));
        dto.setAwayRating(buildTeamRating(awayId, teamsMap.get(awayId), as, hs, maxGoals, events, match));

        // MVP
        if (dto.getHomeRating().getScore() >= dto.getAwayRating().getScore()) {
            dto.setMvpTeamId(homeId);
            dto.setMvpTeamName(dto.getHomeRating().getTeamName());
        } else {
            dto.setMvpTeamId(awayId);
            dto.setMvpTeamName(dto.getAwayRating().getTeamName());
        }
        return dto;
    }

    private MatchRatingDto.TeamRating buildTeamRating(Long teamId, Team team, int goalsFor,
                                                       int goalsAgainst, int maxGoals,
                                                       List<MatchEvent> events, Match match) {
        // Offensive 35%: goals ratio
        double offensive = maxGoals > 0 ? (double) goalsFor / (maxGoals + 1) * 10 : (goalsFor > 0 ? 5.0 : 2.0);
        offensive = Math.min(10, offensive * 1.5);

        // Defensive 25%: conceded ratio
        double defensive = 10.0 - (double) goalsAgainst / (maxGoals + 1) * 10;
        defensive = Math.max(0, Math.min(10, defensive));

        // Shooting 20%: goal events for this team
        long teamGoalEvents = events.stream()
                .filter(e -> teamId.equals(e.getTeamId()) && (MatchEventType.GOAL.equals(e.getEventType()) || MatchEventType.SCORE.equals(e.getEventType())))
                .count();
        double shooting = Math.min(10, teamGoalEvents * 2.5);

        // Discipline 10%: penalties from cards
        long yellows = events.stream().filter(e -> teamId.equals(e.getTeamId()) && (MatchEventType.YELLOW_CARD.equals(e.getEventType()) || MatchEventType.WARNING.equals(e.getEventType()))).count();
        long reds = events.stream().filter(e -> teamId.equals(e.getTeamId()) && (MatchEventType.RED_CARD.equals(e.getEventType()) || MatchEventType.EJECTION.equals(e.getEventType()))).count();
        double discipline = Math.max(0, 10.0 - (yellows * 1.0 + reds * 3.0));

        // Momentum 10%: last team to score gets bonus
        double momentum = 5.0; // neutral baseline
        List<MatchEvent> goals = events.stream()
                .filter(e -> MatchEventType.GOAL.equals(e.getEventType()) || MatchEventType.SCORE.equals(e.getEventType())).toList();
        if (!goals.isEmpty()) {
            Long lastScorer = goals.get(goals.size() - 1).getTeamId();
            if (teamId.equals(lastScorer)) momentum = 10.0;
            else momentum = 2.0;
        }

        // Weighted score
        double score = offensive * 0.35 + defensive * 0.25 + shooting * 0.20 + discipline * 0.10 + momentum * 0.10;
        score = Math.round(score * 10.0) / 10.0;

        String grade;
        if (score >= 9) grade = "A+";
        else if (score >= 8) grade = "A";
        else if (score >= 7) grade = "B";
        else if (score >= 6) grade = "C";
        else grade = "D";

        MatchRatingDto.TeamRating rating = new MatchRatingDto.TeamRating();
        rating.setTeamId(teamId);
        rating.setTeamName(team != null ? team.getName() : "Team " + teamId);
        rating.setScore(score);
        rating.setGrade(grade);
        rating.setBreakdown(new MatchRatingDto.RatingBreakdown(
                Math.round(offensive * 10.0) / 10.0,
                Math.round(defensive * 10.0) / 10.0,
                Math.round(shooting * 10.0) / 10.0,
                Math.round(discipline * 10.0) / 10.0,
                Math.round(momentum * 10.0) / 10.0));
        return rating;
    }

    // ─── MÉTIER 2 — Match Scheduler ──────────────────────────────────────────

    public ScheduleResultDto scheduleMatches(Long competitionId, ScheduleRequestDto request) {
        List<Match> toSchedule = matchRepository.findByCompetitionIdAndStatus(
                competitionId, MatchStatus.SCHEDULED);

        ScheduleResultDto result = new ScheduleResultDto();
        List<ScheduleResultDto.ScheduledMatchEntry> scheduled = new ArrayList<>();
        List<ScheduleResultDto.ConflictEntry> conflicts = new ArrayList<>();

        Map<Long, Team> teamsMap = new HashMap<>();
        Set<Long> allTeamIds = new HashSet<>();
        for (Match m : toSchedule) { allTeamIds.add(m.getHomeTeamId()); allTeamIds.add(m.getAwayTeamId()); }
        teamRepository.findAllById(new ArrayList<>(allTeamIds)).forEach(t -> teamsMap.put(t.getId(), t));

        LocalDate current = request.getStartDate();
        LocalDate endDate = request.getEndDate();
        int maxPerDay = request.getMaxMatchesPerDay() > 0 ? request.getMaxMatchesPerDay() : 3;
        int intervalMins = request.getIntervalMinutes() > 0 ? request.getIntervalMinutes() : 90;

        // Track: date -> {matchCount, teamIds playing that day}
        Map<LocalDate, Integer> matchCountPerDay = new HashMap<>();
        Map<LocalDate, Set<Long>> teamsPerDay = new HashMap<>();

        LocalTime startTime = LocalTime.of(10, 0); // default start time

        for (Match m : toSchedule) {
            boolean placed = false;
            LocalDate day = current;

            while (!day.isAfter(endDate) && !placed) {
                int count = matchCountPerDay.getOrDefault(day, 0);
                Set<Long> teamsToday = teamsPerDay.getOrDefault(day, new HashSet<>());

                boolean homeConflict = teamsToday.contains(m.getHomeTeamId());
                boolean awayConflict = teamsToday.contains(m.getAwayTeamId());

                if (count < maxPerDay && !homeConflict && !awayConflict) {
                    // Schedule it
                    LocalDateTime slot = day.atTime(startTime.plusMinutes((long) count * intervalMins));
                    m.setScheduledAt(slot);
                    matchRepository.save(m);

                    matchCountPerDay.put(day, count + 1);
                    teamsPerDay.computeIfAbsent(day, k -> new HashSet<>()).add(m.getHomeTeamId());
                    teamsPerDay.computeIfAbsent(day, k -> new HashSet<>()).add(m.getAwayTeamId());

                    Team ht = teamsMap.get(m.getHomeTeamId()), at = teamsMap.get(m.getAwayTeamId());
                    ScheduleResultDto.ScheduledMatchEntry entry = new ScheduleResultDto.ScheduledMatchEntry();
                    entry.setMatchId(m.getId());
                    entry.setHomeTeamId(m.getHomeTeamId());
                    entry.setHomeTeamName(ht != null ? ht.getName() : "Team " + m.getHomeTeamId());
                    entry.setAwayTeamId(m.getAwayTeamId());
                    entry.setAwayTeamName(at != null ? at.getName() : "Team " + m.getAwayTeamId());
                    entry.setScheduledAt(slot);
                    scheduled.add(entry);
                    placed = true;
                } else {
                    // Record conflicts
                    if (homeConflict) {
                        Team ht = teamsMap.get(m.getHomeTeamId());
                        conflicts.add(new ScheduleResultDto.ConflictEntry(
                                m.getHomeTeamId(), ht != null ? ht.getName() : "Team " + m.getHomeTeamId(),
                                day.toString(), "Équipe joue déjà ce jour"));
                    }
                    if (awayConflict) {
                        Team at = teamsMap.get(m.getAwayTeamId());
                        conflicts.add(new ScheduleResultDto.ConflictEntry(
                                m.getAwayTeamId(), at != null ? at.getName() : "Team " + m.getAwayTeamId(),
                                day.toString(), "Équipe joue déjà ce jour"));
                    }
                    day = day.plusDays(1);
                }
            }

            if (!placed) {
                conflicts.add(new ScheduleResultDto.ConflictEntry(
                        m.getId(), "Match #" + m.getId(), endDate.toString(), "Impossible de planifier avant la date de fin"));
            }
        }

        result.setScheduledMatches(scheduled);
        result.setConflicts(conflicts);
        result.setTotalScheduled(scheduled.size());
        result.setTotalConflicts(conflicts.size());
        return result;
    }
}
