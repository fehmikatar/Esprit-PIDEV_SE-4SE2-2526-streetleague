package tn.esprit._4se2.pi.services.Sentiment;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class CommunitySentimentTrend {
    private Long communityId;
    private double averageSentiment;
    private double trend;
    private Map<String, Double> aspectAverages;
    private int periodDays;
}