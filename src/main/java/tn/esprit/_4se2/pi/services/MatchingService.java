package tn.esprit._4se2.pi.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import tn.esprit._4se2.pi.dto.MatchingDTOs;
import tn.esprit._4se2.pi.entities.MatchScore;
import tn.esprit._4se2.pi.entities.Player;
import tn.esprit._4se2.pi.entities.Team;
import tn.esprit._4se2.pi.entities.enums.PlayPosition;
import tn.esprit._4se2.pi.entities.enums.SkillLevel;
import tn.esprit._4se2.pi.entities.enums.SportType;
import tn.esprit._4se2.pi.repositories.MatchScoreRepository;
import tn.esprit._4se2.pi.repositories.PlayerRepository;
import tn.esprit._4se2.pi.repositories.TeamRepository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MatchingService {

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;
    private final MatchScoreRepository matchScoreRepository;

    private static final int MAX_DISTANCE_KM = 50;

    /**
     * Compute best teams for a given player based on a scoring algorithm.
     */
    public List<MatchingDTOs.MatchResponse> getBestTeamsForPlayer(Long playerId, int limit) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Player not found"));

        SportType targetSport = player.getSportType();

        List<Team> candidateTeams = teamRepository.findAll()
                .stream()
                .filter(t -> targetSport == null || targetSport == t.getSport())
                .collect(Collectors.toList());

        return candidateTeams.stream()
                .map(team -> calculateMatchScore(player, team))
                .sorted(Comparator.comparingDouble(MatchingDTOs.MatchResponse::getScore).reversed())
                .limit(limit)
                .peek(dto -> saveMatchHistory(player, teamRepository.getReferenceById(dto.getId()), dto.getScore()))
                .collect(Collectors.toList());
    }

    /**
     * Compute best players for a given team based on a scoring algorithm.
     */
    public List<MatchingDTOs.MatchResponse> getBestPlayersForTeam(Long teamId, int limit) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Team not found"));

        SportType targetSport = team.getSport();

        List<Player> candidatePlayers = playerRepository.findAll()
                .stream()
                .filter(p -> targetSport == null || targetSport == p.getSportType())
                .collect(Collectors.toList());

        return candidatePlayers.stream()
                .map(player -> calculateMatchScoreForTeam(player, team))
                .sorted(Comparator.comparingDouble(MatchingDTOs.MatchResponse::getScore).reversed())
                .limit(limit)
                .peek(dto -> saveMatchHistory(playerRepository.getReferenceById(dto.getId()), team, dto.getScore()))
                .collect(Collectors.toList());
    }

            /**
             * Compute best teams from a profile payload (without requiring a persisted player row).
             */
            public List<MatchingDTOs.MatchResponse> getBestTeamsForProfile(MatchingDTOs.PlayerProfileRequest profile, int limit) {
            if (profile == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Profile payload is required");
            }

            SportType targetSport = parseSportType(profile.getSportType());
            SkillLevel skillLevel = parseSkillLevel(profile.getSkillLevel());
            PlayPosition playPosition = parsePlayPosition(profile.getPosition());

            List<MatchingDTOs.AvailabilityInput> availability = profile.getAvailability() == null
                ? List.of()
                : profile.getAvailability();

            List<Team> candidateTeams = teamRepository.findAll()
                .stream()
                .filter(t -> targetSport == null || targetSport == t.getSport())
                .collect(Collectors.toList());

            return candidateTeams.stream()
                .map(team -> calculateMatchScoreFromProfile(profile, skillLevel, playPosition, availability, team))
                .sorted(Comparator.comparingDouble(MatchingDTOs.MatchResponse::getScore).reversed())
                .limit(limit)
                .collect(Collectors.toList());
            }

    /**
     * Computes matching score. 0-100 scale.
     * Weights:
     * - Skill compatibility: 30%
     * - Location proximity: 20%
     * - Availability match: 20%
     * - Position need: 20%
     * - Team compatibility / Rating: 10%
     */
    private MatchingDTOs.MatchResponse calculateMatchScore(Player player, Team team) {
        double score = 0;

        // 1. Skill compatibility (30)
        double skillScore = calculateSkillMatch(player.getSkillLevel(), team.getLevel());
        score += skillScore * 0.30;

        // 2. Location proximity (20)
        double distance = calculateHaversine(player.getLatitude(), player.getLongitude(),
                team.getLatitude(), team.getLongitude());
        double locationScore = 0;
        if (distance >= 0 && distance <= MAX_DISTANCE_KM) {
            locationScore = 100 * (1 - (distance / MAX_DISTANCE_KM));
        }
        score += locationScore * 0.20;

        // 3. Availability match (20)
        double availabilityScore = calculateAvailabilityMatch(player.getAvailability(), team.getSchedule());
        score += availabilityScore * 0.20;

        // 4. Position need (20)
        double positionScore = calculatePositionMatch(player.getPosition(), team.getRequiredPositions());
        score += positionScore * 0.20;

        // 5. Past performance / Rating (10)
        double ratingScore = player.getRating() != null ? (player.getRating() / 5.0) * 100 : 50; // assuming rating out of 5
        score += ratingScore * 0.10;

        return MatchingDTOs.MatchResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .score(Math.round(score * 10.0) / 10.0)
                .distanceKm(Math.round(distance * 10.0) / 10.0)
                .matchDetails("Match based on " + (int)skillScore + "% skill, " + 
                              (int)locationScore + "% location, and " + (int)availabilityScore + "% schedule overlap.")
                .build();
    }

    private MatchingDTOs.MatchResponse calculateMatchScoreForTeam(Player player, Team team) {
        MatchingDTOs.MatchResponse teamMatch = calculateMatchScore(player, team);
        // Switch identity back to player for the response output
        teamMatch.setId(player.getId());
        teamMatch.setName(player.getFirstName() + " " + player.getLastName());
        return teamMatch;
    }

    private double calculateSkillMatch(tn.esprit._4se2.pi.entities.enums.SkillLevel playerSkillLevel, tn.esprit._4se2.pi.entities.enums.SkillLevel teamLevelStr) {
        if (playerSkillLevel == null || teamLevelStr == null) return 50.0;
        
        int pLevel = playerSkillLevel.ordinal();
        int tLevel = teamLevelStr.ordinal();

        int diff = Math.abs(pLevel - tLevel);
        if (diff == 0) return 100;
        if (diff == 1) return 50;
        return 0;
    }

    private double calculateAvailabilityMatch(List<tn.esprit._4se2.pi.entities.AvailabilitySlot> pAvail, List<tn.esprit._4se2.pi.entities.AvailabilitySlot> tAvail) {
        if (pAvail == null || tAvail == null || pAvail.isEmpty() || tAvail.isEmpty()) return 50;
        
        // Exact day match check
        for (tn.esprit._4se2.pi.entities.AvailabilitySlot pSlot : pAvail) {
            for (tn.esprit._4se2.pi.entities.AvailabilitySlot tSlot : tAvail) {
                if (pSlot.getDayOfWeek() == tSlot.getDayOfWeek()) return 100; // Partial simple match
            }
        }
        return 20;
    }

    private double calculatePositionMatch(tn.esprit._4se2.pi.entities.enums.PlayPosition playerPos, List<tn.esprit._4se2.pi.entities.PositionRequirement> requiredPos) {
        if (requiredPos == null || requiredPos.isEmpty()) return 100; // Team accepts anyone
        if (playerPos == null) return 50;
        
        for (tn.esprit._4se2.pi.entities.PositionRequirement req : requiredPos) {
            if (req.getPosition() == playerPos) return 100;
        }
        return 0; // Not looking for this position
    }

    private double calculateHaversine(Double lat1, Double lon1, Double lat2, Double lon2) {
        if (lat1 == null || lon1 == null || lat2 == null || lon2 == null) {
            return 999.0; // Return dummy large distance
        }

        final int R = 6371; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private void saveMatchHistory(Player player, Team team, double score) {
        try {
            MatchScore matchScore = MatchScore.builder()
                    .player(player)
                    .team(team)
                    .score(score)
                    .computedAt(LocalDateTime.now())
                    .build();
            matchScoreRepository.save(matchScore);
        } catch (Exception e) {
            log.warn("Failed to save match history: {}", e.getMessage());
        }
    }

    private MatchingDTOs.MatchResponse calculateMatchScoreFromProfile(
            MatchingDTOs.PlayerProfileRequest profile,
            SkillLevel skillLevel,
            PlayPosition playPosition,
            List<MatchingDTOs.AvailabilityInput> availability,
            Team team
    ) {
        double score = 0;

        // 1. Skill compatibility (30)
        double skillScore = calculateSkillMatch(skillLevel, team.getLevel());
        score += skillScore * 0.30;

        // 2. Location proximity (20)
        double distance = calculateHaversine(profile.getLatitude(), profile.getLongitude(), team.getLatitude(), team.getLongitude());
        double locationScore = 0;
        if (distance >= 0 && distance <= MAX_DISTANCE_KM) {
            locationScore = 100 * (1 - (distance / MAX_DISTANCE_KM));
        }
        score += locationScore * 0.20;

        // 3. Availability match (20)
        double availabilityScore = calculateAvailabilityMatchFromProfile(availability, team.getSchedule());
        score += availabilityScore * 0.20;

        // 4. Position need (20)
        double positionScore = calculatePositionMatch(playPosition, team.getRequiredPositions());
        score += positionScore * 0.20;

        // 5. Rating (10)
        double ratingScore = profile.getRating() != null ? (profile.getRating() / 5.0) * 100 : 50;
        score += ratingScore * 0.10;

        return MatchingDTOs.MatchResponse.builder()
                .id(team.getId())
                .name(team.getName())
                .score(Math.round(score * 10.0) / 10.0)
                .distanceKm(Math.round(distance * 10.0) / 10.0)
                .matchDetails("Match based on " + (int) skillScore + "% skill, " +
                        (int) locationScore + "% location, and " + (int) availabilityScore + "% schedule overlap.")
                .build();
    }

    private double calculateAvailabilityMatchFromProfile(
            List<MatchingDTOs.AvailabilityInput> profileAvailability,
            List<tn.esprit._4se2.pi.entities.AvailabilitySlot> teamAvailability
    ) {
        if (profileAvailability == null || teamAvailability == null || profileAvailability.isEmpty() || teamAvailability.isEmpty()) {
            return 50;
        }

        for (MatchingDTOs.AvailabilityInput pSlot : profileAvailability) {
            DayOfWeek pDay = parseDayOfWeek(pSlot.getDayOfWeek());
            LocalTime pStart = parseTime(pSlot.getStartTime());
            LocalTime pEnd = parseTime(pSlot.getEndTime());

            if (pDay == null || pStart == null || pEnd == null || !pStart.isBefore(pEnd)) {
                continue;
            }

            for (tn.esprit._4se2.pi.entities.AvailabilitySlot tSlot : teamAvailability) {
                if (tSlot == null || tSlot.getDayOfWeek() == null || tSlot.getStartTime() == null || tSlot.getEndTime() == null) {
                    continue;
                }
                if (pDay != tSlot.getDayOfWeek()) {
                    continue;
                }

                boolean overlap = pStart.isBefore(tSlot.getEndTime()) && pEnd.isAfter(tSlot.getStartTime());
                if (overlap) {
                    return 100;
                }
            }
        }

        return 20;
    }

    private SportType parseSportType(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String normalized = value.trim().toUpperCase();
        if ("PADEL".equals(normalized)) {
            return SportType.OTHER;
        }
        try {
            return SportType.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            return SportType.OTHER;
        }
    }

    private SkillLevel parseSkillLevel(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return SkillLevel.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private PlayPosition parsePlayPosition(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = value.trim().toUpperCase();
        if ("FORWARD".equals(normalized)) {
            return PlayPosition.STRIKER;
        }
        if ("LEFT_SIDE".equals(normalized) || "RIGHT_SIDE".equals(normalized) || "SINGLES".equals(normalized) || "DOUBLES_SPECIALIST".equals(normalized)) {
            return PlayPosition.ANY;
        }
        if ("OUTSIDE_HITTER".equals(normalized) || "OPPOSITE".equals(normalized) || "MIDDLE_BLOCKER".equals(normalized) || "LIBERO".equals(normalized)
                || "SETTER".equals(normalized) || "LEFT_WING".equals(normalized) || "RIGHT_WING".equals(normalized)
                || "PIVOT".equals(normalized) || "BACK".equals(normalized)) {
            return PlayPosition.ANY;
        }

        try {
            return PlayPosition.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            return PlayPosition.ANY;
        }
    }

    private DayOfWeek parseDayOfWeek(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return DayOfWeek.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private LocalTime parseTime(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalTime.parse(value.trim());
        } catch (Exception ex) {
            return null;
        }
    }
}
