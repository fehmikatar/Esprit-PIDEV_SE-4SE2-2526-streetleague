package tn.esprit._4se2.pi.dto.Sentiment;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

public class SentimentDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AnalyzeTextRequest {
        @NotBlank
        private String text;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SentimentResponse {
        private String sentiment;
        private double score;
        private String intensity;
        private Map<String, Double> aspects;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CommunityTrendResponse {
        private Long communityId;
        private double averageSentiment;
        private double trend;
        private Map<String, Double> aspectAverages;
        private int periodDays;
    }
}