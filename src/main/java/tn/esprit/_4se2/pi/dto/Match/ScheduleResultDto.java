package tn.esprit._4se2.pi.dto.Match;

import java.time.LocalDateTime;
import java.util.List;

public class ScheduleResultDto {
    private int totalScheduled;
    private int totalConflicts;
    private List<ScheduledMatchEntry> scheduledMatches;
    private List<ConflictEntry> conflicts;

    public static class ScheduledMatchEntry {
        private Long matchId;
        private Long homeTeamId;
        private String homeTeamName;
        private Long awayTeamId;
        private String awayTeamName;
        private LocalDateTime scheduledAt;

        public ScheduledMatchEntry() {}
        public Long getMatchId() { return matchId; }
        public void setMatchId(Long matchId) { this.matchId = matchId; }
        public Long getHomeTeamId() { return homeTeamId; }
        public void setHomeTeamId(Long homeTeamId) { this.homeTeamId = homeTeamId; }
        public String getHomeTeamName() { return homeTeamName; }
        public void setHomeTeamName(String homeTeamName) { this.homeTeamName = homeTeamName; }
        public Long getAwayTeamId() { return awayTeamId; }
        public void setAwayTeamId(Long awayTeamId) { this.awayTeamId = awayTeamId; }
        public String getAwayTeamName() { return awayTeamName; }
        public void setAwayTeamName(String awayTeamName) { this.awayTeamName = awayTeamName; }
        public LocalDateTime getScheduledAt() { return scheduledAt; }
        public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }
    }

    public static class ConflictEntry {
        private Long teamId;
        private String teamName;
        private String conflictDate;
        private String reason;

        public ConflictEntry() {}
        public ConflictEntry(Long teamId, String teamName, String conflictDate, String reason) {
            this.teamId = teamId; this.teamName = teamName;
            this.conflictDate = conflictDate; this.reason = reason;
        }
        public Long getTeamId() { return teamId; }
        public void setTeamId(Long teamId) { this.teamId = teamId; }
        public String getTeamName() { return teamName; }
        public void setTeamName(String teamName) { this.teamName = teamName; }
        public String getConflictDate() { return conflictDate; }
        public void setConflictDate(String conflictDate) { this.conflictDate = conflictDate; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }

    public int getTotalScheduled() { return totalScheduled; }
    public void setTotalScheduled(int totalScheduled) { this.totalScheduled = totalScheduled; }
    public int getTotalConflicts() { return totalConflicts; }
    public void setTotalConflicts(int totalConflicts) { this.totalConflicts = totalConflicts; }
    public List<ScheduledMatchEntry> getScheduledMatches() { return scheduledMatches; }
    public void setScheduledMatches(List<ScheduledMatchEntry> scheduledMatches) { this.scheduledMatches = scheduledMatches; }
    public List<ConflictEntry> getConflicts() { return conflicts; }
    public void setConflicts(List<ConflictEntry> conflicts) { this.conflicts = conflicts; }
}
