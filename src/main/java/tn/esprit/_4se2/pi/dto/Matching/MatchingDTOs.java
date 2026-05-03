package tn.esprit._4se2.pi.dto.Matching;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

public class MatchingDTOs {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AvailabilityInput {
        private String dayOfWeek;
        private String startTime;
        private String endTime;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PlayerProfileMatchingRequest {
        private String type; // PLAYER
        private String name;
        private String sportType;
        private String skillLevel;
        private String position;
        private String city;
        private Double latitude;
        private Double longitude;
        private List<AvailabilityInput> availability;
        private String preferredPlayStyle;
        private Double rating;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatchResponse {
        private Long id;
        private String name;
        private Double score;
        private String matchDetails;
        private Double distanceKm;
    }
}
