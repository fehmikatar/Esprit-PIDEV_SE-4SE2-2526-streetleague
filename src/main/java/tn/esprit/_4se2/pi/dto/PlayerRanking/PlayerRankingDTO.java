package tn.esprit._4se2.pi.dto.PlayerRanking;



import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlayerRankingDTO {
    private Long playerId;
    private String firstName;
    private String lastName;
    private String email;
    private Integer currentLevel;
    private Integer totalXp;
    private Integer gamesPlayed;
    private Integer xpPerGame; // ratio arrondi
}