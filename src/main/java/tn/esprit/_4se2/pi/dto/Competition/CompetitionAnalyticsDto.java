package tn.esprit._4se2.pi.dto.Competition;

import java.util.List;
import java.util.Map;

public class CompetitionAnalyticsDto {

    // Best attack: most goals scored
    private TeamStatEntry bestAttack;

    // Best defense: fewest goals conceded
    private TeamStatEntry bestDefense;

    // Top scorers (by playerId)
    private List<PlayerGoalEntry> topScorers;

    // Number of upsets (high seed eliminated by low seed)
    private int totalUpsets;

    // Cards summary per team
    private List<TeamCardSummary> cardsSummary;

    // Overall totals
    private int totalGoals;
    private int totalWarnings;
    private int totalEjections;
    private int totalMatches;
    private int finishedMatches;

    // --- Nested DTOs ---

    public static class TeamStatEntry {
        private Long teamId;
        private String teamName;
        private int value; // goals scored or goals conceded

        public TeamStatEntry() {}
        public TeamStatEntry(Long teamId, String teamName, int value) {
            this.teamId = teamId; this.teamName = teamName; this.value = value;
        }
        public Long getTeamId() { return teamId; }
        public void setTeamId(Long teamId) { this.teamId = teamId; }
        public String getTeamName() { return teamName; }
        public void setTeamName(String teamName) { this.teamName = teamName; }
        public int getValue() { return value; }
        public void setValue(int value) { this.value = value; }
    }

    public static class PlayerGoalEntry {
        private Long playerId;
        private String playerName;
        private int goals;
        private int assists;

        public PlayerGoalEntry() {}
        public PlayerGoalEntry(Long playerId, String playerName, int goals, int assists) {
            this.playerId = playerId; this.playerName = playerName;
            this.goals = goals; this.assists = assists;
        }
        public Long getPlayerId() { return playerId; }
        public void setPlayerId(Long playerId) { this.playerId = playerId; }
        public String getPlayerName() { return playerName; }
        public void setPlayerName(String playerName) { this.playerName = playerName; }
        public int getGoals() { return goals; }
        public void setGoals(int goals) { this.goals = goals; }
        public int getAssists() { return assists; }
        public void setAssists(int assists) { this.assists = assists; }
    }

    public static class TeamCardSummary {
        private Long teamId;
        private String teamName;
        private int warnings;
        private int ejections;

        public TeamCardSummary() {}
        public TeamCardSummary(Long teamId, String teamName, int warnings, int ejections) {
            this.teamId = teamId; this.teamName = teamName;
            this.warnings = warnings; this.ejections = ejections;
        }
        public Long getTeamId() { return teamId; }
        public void setTeamId(Long teamId) { this.teamId = teamId; }
        public String getTeamName() { return teamName; }
        public void setTeamName(String teamName) { this.teamName = teamName; }
        public int getWarnings() { return warnings; }
        public void setWarnings(int warnings) { this.warnings = warnings; }
        public int getEjections() { return ejections; }
        public void setEjections(int ejections) { this.ejections = ejections; }
    }

    // Getters/Setters
    public TeamStatEntry getBestAttack() { return bestAttack; }
    public void setBestAttack(TeamStatEntry bestAttack) { this.bestAttack = bestAttack; }
    public TeamStatEntry getBestDefense() { return bestDefense; }
    public void setBestDefense(TeamStatEntry bestDefense) { this.bestDefense = bestDefense; }
    public List<PlayerGoalEntry> getTopScorers() { return topScorers; }
    public void setTopScorers(List<PlayerGoalEntry> topScorers) { this.topScorers = topScorers; }
    public int getTotalUpsets() { return totalUpsets; }
    public void setTotalUpsets(int totalUpsets) { this.totalUpsets = totalUpsets; }
    public List<TeamCardSummary> getCardsSummary() { return cardsSummary; }
    public void setCardsSummary(List<TeamCardSummary> cardsSummary) { this.cardsSummary = cardsSummary; }
    public int getTotalGoals() { return totalGoals; }
    public void setTotalGoals(int totalGoals) { this.totalGoals = totalGoals; }
    public int getTotalWarnings() { return totalWarnings; }
    public void setTotalWarnings(int totalWarnings) { this.totalWarnings = totalWarnings; }
    public int getTotalEjections() { return totalEjections; }
    public void setTotalEjections(int totalEjections) { this.totalEjections = totalEjections; }
    public int getTotalMatches() { return totalMatches; }
    public void setTotalMatches(int totalMatches) { this.totalMatches = totalMatches; }
    public int getFinishedMatches() { return finishedMatches; }
    public void setFinishedMatches(int finishedMatches) { this.finishedMatches = finishedMatches; }
}
