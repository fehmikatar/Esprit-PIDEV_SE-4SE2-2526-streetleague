package tn.esprit._4se2.pi.services.Sentiment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SentimentResult {
    private String sentiment;
    private double score;
    private String intensity;
    private Map<String, Double> aspects;

    public SentimentResult(String sentiment, double score, String intensity) {
        this(sentiment, score, intensity, new HashMap<>());
    }
}