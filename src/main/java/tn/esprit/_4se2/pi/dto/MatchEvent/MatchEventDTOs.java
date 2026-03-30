package tn.esprit._4se2.pi.dto.MatchEvent;

import tn.esprit._4se2.pi.Enum.MatchEventType;
import jakarta.validation.constraints.*;
public class MatchEventDTOs {

    // Request DTO
    public class MatchEventRequestDTO {

        @NotNull(message = "L'ID du match est requis")
        private Long matchId;

        @NotNull(message = "Le type d'événement est requis")
        private MatchEventType type;

        @NotNull(message = "La minute est requise")
        @Min(value = 0, message = "La minute ne peut pas être négative")
        @Max(value = 120, message = "La minute ne peut pas dépasser 120")
        private Integer minute;

        private Long teamId;

        private Long playerId;

        @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
        private String description;

        // Getters and Setters
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

    // Response DTO
    public class MatchEventResponseDTO {
        private Long id;
        private Long matchId;
        private MatchEventType type;
        private Integer minute;
        private Long teamId;
        private String teamName;
        private Long playerId;
        private String playerName;
        private String description;

        // Getters and Setters
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

        public String getTeamName() { return teamName; }
        public void setTeamName(String teamName) { this.teamName = teamName; }

        public Long getPlayerId() { return playerId; }
        public void setPlayerId(Long playerId) { this.playerId = playerId; }

        public String getPlayerName() { return playerName; }
        public void setPlayerName(String playerName) { this.playerName = playerName; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}
