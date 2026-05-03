package tn.esprit._4se2.pi.dto.Match;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * DTO reçu en retour depuis l'API FastAPI Python.
 */
public class MatchPredictionResponseDto {

    /** HOME_WIN | AWAY_WIN | DRAW */
    private String result;

    /** Pourcentage de confiance (ex: 78.5) */
    private double confidence;

    /** Probabilités par classe : {"HOME_WIN": 78.5, "AWAY_WIN": 14.2, "DRAW": 7.3} */
    private Map<String, Double> probabilities;

    /** Texte explicatif lisible */
    private String interpretation;

    public MatchPredictionResponseDto() {}

    // ── Getters / Setters ────────────────────────────────────
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }

    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }

    public Map<String, Double> getProbabilities() { return probabilities; }
    public void setProbabilities(Map<String, Double> probabilities) { this.probabilities = probabilities; }

    public String getInterpretation() { return interpretation; }
    public void setInterpretation(String interpretation) { this.interpretation = interpretation; }
}
