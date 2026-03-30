package tn.esprit._4se2.pi.dto;

import tn.esprit._4se2.pi.Enum.MatchStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
public class MatchDTOs {
    // Request DTO
    public class MatchRequestDTO {

        @NotNull(message = "L'ID de la compétition est requis")
        private Long competitionId;

        @NotNull(message = "L'ID de l'équipe domicile est requis")
        private Long homeTeamId;

        @NotNull(message = "L'ID de l'équipe extérieure est requis")
        private Long awayTeamId;

        @NotNull(message = "La date et l'heure du match sont requises")
        @Future(message = "La date du match doit être dans le futur")
        private LocalDateTime scheduledAt;

        @NotBlank(message = "Le lieu du match est requis")
        private String venue;

        private MatchStatus status;

        // Getters and Setters
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
    }

    // Response DTO
    public class MatchResponseDTO {
        private Long id;
        private Long competitionId;
        private String competitionName;
        private Long homeTeamId;
        private String homeTeamName;
        private Long awayTeamId;
        private String awayTeamName;
        private LocalDateTime scheduledAt;
        private String venue;
        private MatchStatus status;
        private Integer homeScore;
        private Integer awayScore;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getCompetitionId() { return competitionId; }
        public void setCompetitionId(Long competitionId) { this.competitionId = competitionId; }

        public String getCompetitionName() { return competitionName; }
        public void setCompetitionName(String competitionName) { this.competitionName = competitionName; }

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

        public String getVenue() { return venue; }
        public void setVenue(String venue) { this.venue = venue; }

        public MatchStatus getStatus() { return status; }
        public void setStatus(MatchStatus status) { this.status = status; }

        public Integer getHomeScore() { return homeScore; }
        public void setHomeScore(Integer homeScore) { this.homeScore = homeScore; }

        public Integer getAwayScore() { return awayScore; }
        public void setAwayScore(Integer awayScore) { this.awayScore = awayScore; }
    }

    // Update Score DTO
    public class MatchScoreUpdateDTO {

        @Min(value = 0, message = "Le score domicile ne peut pas être négatif")
        private Integer homeScore;

        @Min(value = 0, message = "Le score extérieur ne peut pas être négatif")
        private Integer awayScore;

        // Getters and Setters
        public Integer getHomeScore() { return homeScore; }
        public void setHomeScore(Integer homeScore) { this.homeScore = homeScore; }

        public Integer getAwayScore() { return awayScore; }
        public void setAwayScore(Integer awayScore) { this.awayScore = awayScore; }
    }
}
