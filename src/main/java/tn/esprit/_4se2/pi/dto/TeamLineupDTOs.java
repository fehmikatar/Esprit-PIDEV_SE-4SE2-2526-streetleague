package tn.esprit._4se2.pi.dto;

import jakarta.validation.constraints.*;
import java.util.List;
public class TeamLineupDTOs {

    // Request DTO
    public class TeamLineupRequestDTO {

        @NotNull(message = "L'ID du match est requis")
        private Long matchId;

        @NotNull(message = "L'ID de l'équipe est requis")
        private Long teamId;

        @Pattern(regexp = "^\\d-\\d-\\d$", message = "La formation doit être au format comme 4-4-2")
        private String formation;

        @NotEmpty(message = "La liste des joueurs titulaires est requise")
        @Size(min = 11, max = 11, message = "Il doit y avoir exactement 11 joueurs titulaires")
        private List<Long> startingPlayerIds;

        @Size(max = 15, message = "Maximum 15 joueurs remplaçants")
        private List<Long> substitutePlayerIds;

        @NotNull(message = "L'ID du capitaine est requis")
        private Long captainId;

        // Getters and Setters
        public Long getMatchId() { return matchId; }
        public void setMatchId(Long matchId) { this.matchId = matchId; }

        public Long getTeamId() { return teamId; }
        public void setTeamId(Long teamId) { this.teamId = teamId; }

        public String getFormation() { return formation; }
        public void setFormation(String formation) { this.formation = formation; }

        public List<Long> getStartingPlayerIds() { return startingPlayerIds; }
        public void setStartingPlayerIds(List<Long> startingPlayerIds) { this.startingPlayerIds = startingPlayerIds; }

        public List<Long> getSubstitutePlayerIds() { return substitutePlayerIds; }
        public void setSubstitutePlayerIds(List<Long> substitutePlayerIds) { this.substitutePlayerIds = substitutePlayerIds; }

        public Long getCaptainId() { return captainId; }
        public void setCaptainId(Long captainId) { this.captainId = captainId; }
    }

    // Response DTO
    public class TeamLineupResponseDTO {
        private Long id;
        private Long matchId;
        private Long teamId;
        private String teamName;
        private String formation;
        private List<PlayerInfoDTO> startingXI;
        private List<PlayerInfoDTO> substitutes;
        private PlayerInfoDTO captain;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getMatchId() { return matchId; }
        public void setMatchId(Long matchId) { this.matchId = matchId; }

        public Long getTeamId() { return teamId; }
        public void setTeamId(Long teamId) { this.teamId = teamId; }

        public String getTeamName() { return teamName; }
        public void setTeamName(String teamName) { this.teamName = teamName; }

        public String getFormation() { return formation; }
        public void setFormation(String formation) { this.formation = formation; }

        public List<PlayerInfoDTO> getStartingXI() { return startingXI; }
        public void setStartingXI(List<PlayerInfoDTO> startingXI) { this.startingXI = startingXI; }

        public List<PlayerInfoDTO> getSubstitutes() { return substitutes; }
        public void setSubstitutes(List<PlayerInfoDTO> substitutes) { this.substitutes = substitutes; }

        public PlayerInfoDTO getCaptain() { return captain; }
        public void setCaptain(PlayerInfoDTO captain) { this.captain = captain; }
    }

    // Player Info DTO
    public class PlayerInfoDTO {
        private Long id;
        private String name;
        private String position;
        private Integer number;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getPosition() { return position; }
        public void setPosition(String position) { this.position = position; }

        public Integer getNumber() { return number; }
        public void setNumber(Integer number) { this.number = number; }
    }
}
