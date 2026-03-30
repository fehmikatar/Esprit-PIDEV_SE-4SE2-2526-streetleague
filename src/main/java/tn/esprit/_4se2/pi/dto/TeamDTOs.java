package tn.esprit._4se2.pi.dto;

import jakarta.validation.constraints.*;
import java.util.List;
public class TeamDTOs {

    // Request DTO
    public class TeamRequestDTO {

        @NotBlank(message = "Le nom de l'équipe est requis")
        @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
        private String name;

        @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
        private String description;

        @Pattern(regexp = "^(http|https)://.*$", message = "L'URL du logo doit être valide")
        private String logoUrl;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getLogoUrl() { return logoUrl; }
        public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }
    }

    // Response DTO
    public class TeamResponseDTO {
        private Long id;
        private String name;
        private String description;
        private String logoUrl;
        private Integer totalMatches;
        private Integer wins;
        private Integer draws;
        private Integer losses;
        private Integer goalsScored;
        private Integer goalsConceded;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getLogoUrl() { return logoUrl; }
        public void setLogoUrl(String logoUrl) { this.logoUrl = logoUrl; }

        public Integer getTotalMatches() { return totalMatches; }
        public void setTotalMatches(Integer totalMatches) { this.totalMatches = totalMatches; }

        public Integer getWins() { return wins; }
        public void setWins(Integer wins) { this.wins = wins; }

        public Integer getDraws() { return draws; }
        public void setDraws(Integer draws) { this.draws = draws; }

        public Integer getLosses() { return losses; }
        public void setLosses(Integer losses) { this.losses = losses; }

        public Integer getGoalsScored() { return goalsScored; }
        public void setGoalsScored(Integer goalsScored) { this.goalsScored = goalsScored; }

        public Integer getGoalsConceded() { return goalsConceded; }
        public void setGoalsConceded(Integer goalsConceded) { this.goalsConceded = goalsConceded; }
    }
}
