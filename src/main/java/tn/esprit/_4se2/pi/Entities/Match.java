package tn.esprit._4se2.pi.Entities;


import jakarta.persistence.*;
import org.springframework.beans.factory.config.YamlProcessor;
import tn.esprit._4se2.pi.Enum.MatchStatus;

import java.time.LocalDateTime;

@Entity
@Table(name ="matches")
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private Long competitionId;

    @Column(nullable=false)
    private Long homeTeamId;

    @Column(nullable=false)
    private Long awayTeamId;

    @Column(nullable=false)
    private LocalDateTime scheduledAt;

    private String venue;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false)
    private MatchStatus status = MatchStatus.SCHEDULED;

    private Integer homeScore = 0;
    private Integer awayScore = 0;

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCompetitionId() { return competitionId; }
    public void setCompetitionId(Long competitionId) { this.competitionId = competitionId; }
    public Long getHomeTeamId() { return homeTeamId; }
    public void setHomeTeamId(Long homeTeamId) { this.homeTeamId = homeTeamId; }
    public Long getAwayTeamId() { return awayTeamId; }
    public void setAwayTeamId(Long awayTeamId) { this.awayTeamId = awayTeamId; }
    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public void setScheduledAt(LocalDateTime scheduledAt) { this.scheduledAt = scheduledAt; }
    public String getVenue() { return venue; }
    public void setVenue(String venue) { this.venue = venue; }
    public MatchStatus getStatus() { return status; }
    public void setStatus(MatchStatus status) { this.status = status; }
    public Integer getHomeScore() { return homeScore; }
    public void setHomeScore(Integer homeScore) { this.homeScore = homeScore; }
    public Integer getAwayScore() { return awayScore; }
    public void setAwayScore(Integer awayScore) { this.awayScore = awayScore; }
}
