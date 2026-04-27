package tn.esprit._4se2.pi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class MatchingDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AvailabilityInput {
        private String dayOfWeek;
        private String startTime;
        private String endTime;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class PlayerProfileRequest {
        private String type;
        private String name;
        private String sportType;
        private String skillLevel;
        private String position;
        private String city;
        private Double latitude;
        private Double longitude;
        private java.util.List<AvailabilityInput> availability;
        private String preferredPlayStyle;
        private Double rating;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MatchResponse {
        private Long id; // Team ID or Player ID
        private String name; // Team name or Player name
        private double score; // Matching score out of 100
        private String matchDetails; // Short string detailing why it matched
        private Double distanceKm;
    }
}
