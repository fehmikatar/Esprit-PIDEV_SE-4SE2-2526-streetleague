package tn.esprit._4se2.pi.Entities;

import jakarta.persistence.*;
import tn.esprit._4se2.pi.Enum.MatchEventType;

@Entity
@Table(name = "match_events")
public class MatchEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private Long matchId;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private MatchEventType type = MatchEventType.OTHER;

    @Column(nullable=false)
    private Integer minute;

    private Long teamId;
    private Long playerId;

    @Column(length=1000)
    private String description;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMatchId() { return matchId; }
    public void setMatchId(Long matchId) { this.matchId = matchId; }
    public MatchEventType getType() { return type; }
    public void setType(MatchEventType type) { this.type = type; }
    public Integer getMinute() { return minute; }
    public void setMinute(Integer minute) { this.minute = minute; }
    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }
    public Long getPlayerId() { return playerId; }
    public void setPlayerId(Long playerId) { this.playerId = playerId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
