package tn.esprit._4se2.pi.Entities;

import jakarta.persistence.*;

@Entity
@Table(
        name="team_lineups",
        uniqueConstraints = @UniqueConstraint(columnNames = {"matchId","teamId"})
)
public class TeamLineup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false)
    private Long matchId;

    @Column(nullable=false)
    private Long teamId;

    private String formation;


    @Column(length=4000)
    private String playersJson;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getMatchId() { return matchId; }
    public void setMatchId(Long matchId) { this.matchId = matchId; }
    public Long getTeamId() { return teamId; }
    public void setTeamId(Long teamId) { this.teamId = teamId; }
    public String getFormation() { return formation; }
    public void setFormation(String formation) { this.formation = formation; }
    public String getPlayersJson() { return playersJson; }
    public void setPlayersJson(String playersJson) { this.playersJson = playersJson; }
}
