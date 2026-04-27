package tn.esprit._4se2.pi.dto.Recommendation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit._4se2.pi.Enum.RecommendationType;

import java.time.LocalDateTime;

public class RecommendationDTO {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TeamRecommendationResponse {
        private Long teamId;
        private String teamName;
        private String sport;
        private String level;
        private String city;
        private Long categoryId;
        private String categoryName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PostRecommendationResponse {
        private Long postId;
        private String title;
        private String content;
        private Long authorId;
        private String authorFirstName;
        private String authorLastName;
        private Long categoryId;
        private String categoryName;
        private Long communityId;
        private String communityName;
        private LocalDateTime createdAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class RecommendationScoreResponse {
        private RecommendationType recommendationType;
        private String targetType;
        private Long targetId;
        private String sourceType;
        private Long sourceId;
        private Double similarityScore;
        private LocalDateTime calculatedAt;
        private Integer interactionCount;
        private Boolean active;
    }
}