package tn.esprit._4se2.pi.dto.Competition;

import tn.esprit._4se2.pi.Enum.CompetitionFormat;
import tn.esprit._4se2.pi.Enum.CompetitionStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
public class CompetitionDTOs {


    // Request DTO
    public class CompetitionRequestDTO {

        @NotBlank(message = "Le nom de la compétition est requis")
        @Size(min = 3, max = 100, message = "Le nom doit contenir entre 3 et 100 caractères")
        private String name;

        @Size(max = 2000, message = "La description ne peut pas dépasser 2000 caractères")
        private String description;

        @Size(max = 4000, message = "Les règles ne peuvent pas dépasser 4000 caractères")
        private String rules;

        @NotNull(message = "Le format de la compétition est requis")
        private CompetitionFormat format;

        private CompetitionStatus status;

        @Future(message = "La date de début doit être dans le futur")
        private LocalDate startDate;

        @Future(message = "La date de fin doit être dans le futur")
        private LocalDate endDate;

        @Size(max = 255, message = "L'emplacement ne peut pas dépasser 255 caractères")
        private String location;

        private Long organizerId;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getRules() { return rules; }
        public void setRules(String rules) { this.rules = rules; }

        public CompetitionFormat getFormat() { return format; }
        public void setFormat(CompetitionFormat format) { this.format = format; }

        public CompetitionStatus getStatus() { return status; }
        public void setStatus(CompetitionStatus status) { this.status = status; }

        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }

        public Long getOrganizerId() { return organizerId; }
        public void setOrganizerId(Long organizerId) { this.organizerId = organizerId; }
    }

    // Response DTO
    public class CompetitionResponseDTO {
        private Long id;
        private String name;
        private String description;
        private String rules;
        private CompetitionFormat format;
        private CompetitionStatus status;
        private LocalDate startDate;
        private LocalDate endDate;
        private String location;
        private Long organizerId;
        private Integer totalMatches;
        private Integer totalTeams;

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getRules() { return rules; }
        public void setRules(String rules) { this.rules = rules; }

        public CompetitionFormat getFormat() { return format; }
        public void setFormat(CompetitionFormat format) { this.format = format; }

        public CompetitionStatus getStatus() { return status; }
        public void setStatus(CompetitionStatus status) { this.status = status; }

        public LocalDate getStartDate() { return startDate; }
        public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

        public LocalDate getEndDate() { return endDate; }
        public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }

        public Long getOrganizerId() { return organizerId; }
        public void setOrganizerId(Long organizerId) { this.organizerId = organizerId; }

        public Integer getTotalMatches() { return totalMatches; }
        public void setTotalMatches(Integer totalMatches) { this.totalMatches = totalMatches; }

        public Integer getTotalTeams() { return totalTeams; }
        public void setTotalTeams(Integer totalTeams) { this.totalTeams = totalTeams; }
    }
}
