package tn.esprit._4se2.pi.dto.MatchEvent;

import tn.esprit._4se2.pi.Enum.MatchEventType;
import java.util.List;

public class TimelineEventDto {
    private Long id;
    private Long matchId;
    private MatchEventType type;
    private int minute;
    private Long teamId;
    private String teamName;
    private Long playerId;
    private String playerName;
    private String description;
    private Integer points;

    // Enriched fields
    private int homeScoreSnapshot;
    private int awayScoreSnapshot;
    private String momentum;         // "HOME", "AWAY", "NEUTRAL"
    private List<String> anomalies;  // list of anomaly messages

    public TimelineEventDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMatchId() { return matchId; }
    public void setMatchId(Long matchId) { this.matchId = matchId; }
    public MatchEventType getType() { return type; }
    public void setType(MatchEventType type) { this.type = type; }
    public int getMinute() { return minute; }
    public void setMinute(int minute) { this.minute = minute; }
    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }
    public String getTeamName() { return teamName; }
    public void setTeamName(String teamName) { this.teamName = teamName; }
    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }
    public int getHomeScoreSnapshot() { return homeScoreSnapshot; }
    public void setHomeScoreSnapshot(int homeScoreSnapshot) { this.homeScoreSnapshot = homeScoreSnapshot; }
    public int getAwayScoreSnapshot() { return awayScoreSnapshot; }
    public void setAwayScoreSnapshot(int awayScoreSnapshot) { this.awayScoreSnapshot = awayScoreSnapshot; }
    public String getMomentum() { return momentum; }
    public void setMomentum(String momentum) { this.momentum = momentum; }
    public List<String> getAnomalies() { return anomalies; }
    public void setAnomalies(List<String> anomalies) { this.anomalies = anomalies; }
}
