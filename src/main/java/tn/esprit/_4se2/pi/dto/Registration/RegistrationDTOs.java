package tn.esprit._4se2.pi.dto.Registration;

import tn.esprit._4se2.pi.Enum.RegistrationStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
public class RegistrationDTOs {

    // Request DTO
    public class RegistrationRequestDTO {

        @NotNull(message = "L'ID de la compétition est requis")
        private Long competitionId;

        @NotNull(message = "L'ID de l'équipe est requis")
        private Long teamId;

        private RegistrationStatus status;

        // Getters and Setters
        public Long getCompetitionId() { return competitionId; }
        public void setCompetitionId(Long competitionId) { this.competitionId = competitionId; }

        public Long getTeamId() { return teamId; }
        public void setTeamId(Long teamId) { this.teamId = teamId; }

        public RegistrationStatus getStatus() { return status; }
        public void setStatus(RegistrationStatus status) { this.status = status; }
    }

    // Response DTO
    public class RegistrationResponseDTO {
        private Long id;
        private Long competitionId;
        private String competitionName;
        private Long teamId;
        private String teamName;
        private RegistrationStatus status;
        private LocalDateTime createdAt;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public Long getCompetitionId() { return competitionId; }
        public void setCompetitionId(Long competitionId) { this.competitionId = competitionId; }

        public String getCompetitionName() { return competitionName; }
        public void setCompetitionName(String competitionName) { this.competitionName = competitionName; }

        public Long getTeamId() { return teamId; }
        public void setTeamId(Long teamId) { this.teamId = teamId; }

        public String getTeamName() { return teamName; }
        public void setTeamName(String teamName) { this.teamName = teamName; }

        public RegistrationStatus getStatus() { return status; }
        public void setStatus(RegistrationStatus status) { this.status = status; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }

    // Registration Status Update DTO
    public class RegistrationStatusUpdateDTO {

        @NotNull(message = "Le statut est requis")
        private RegistrationStatus status;

        private String rejectionReason;

        // Getters and Setters
        public RegistrationStatus getStatus() { return status; }
        public void setStatus(RegistrationStatus status) { this.status = status; }

        public String getRejectionReason() { return rejectionReason; }
        public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
    }
}
