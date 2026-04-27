package tn.esprit._4se2.pi.dto.Team;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

public class TeamDTOs {

    // Request DTO
    public static class TeamRequestDTO {

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

    // Response DTO - Enhanced with Members
    public static class TeamResponseDTO {
        private Long id;
        private String name;
        private String sport;
        private String level;
        private String description;
        private String logo;
        private Long categoryId;
        private String categoryName;
        private String city;
        private String status;
        private LocalDateTime createdAt;
        private List<TeamMemberDTO> members;
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

        public String getSport() { return sport; }
        public void setSport(String sport) { this.sport = sport; }

        public String getLevel() { return level; }
        public void setLevel(String level) { this.level = level; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getLogo() { return logo; }
        public void setLogo(String logo) { this.logo = logo; }

        public Long getCategoryId() { return categoryId; }
        public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

        public String getCategoryName() { return categoryName; }
        public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

        public String getCity() { return city; }
        public void setCity(String city) { this.city = city; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

        public List<TeamMemberDTO> getMembers() { return members; }
        public void setMembers(List<TeamMemberDTO> members) { this.members = members; }

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

    // Team Member DTO
    public static class TeamMemberDTO {
        private Long userId;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String profileImageUrl;
        private String role; // MEMBER or RESPONSIBLE
        private String position; // Only if user is Player
        private Integer skillLevel; // Only if user is Player
        private Double rating; // Only if user is Player

        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getFirstName() { return firstName; }
        public void setFirstName(String firstName) { this.firstName = firstName; }

        public String getLastName() { return lastName; }
        public void setLastName(String lastName) { this.lastName = lastName; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getProfileImageUrl() { return profileImageUrl; }
        public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public String getPosition() { return position; }
        public void setPosition(String position) { this.position = position; }

        public Integer getSkillLevel() { return skillLevel; }
        public void setSkillLevel(Integer skillLevel) { this.skillLevel = skillLevel; }

        public Double getRating() { return rating; }
        public void setRating(Double rating) { this.rating = rating; }
    }
}
