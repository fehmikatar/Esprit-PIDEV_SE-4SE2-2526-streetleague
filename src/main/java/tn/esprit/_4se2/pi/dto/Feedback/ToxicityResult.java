package tn.esprit._4se2.pi.dto.Feedback;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ToxicityResult {

    String text;

    @JsonProperty("is_toxic")
    boolean toxic;          // ← renommé en "toxic" pour éviter conflit Lombok

    String label;

    @JsonProperty("hate_score")
    double hateScore;

    @JsonProperty("sentiment_score")
    double sentimentScore;

    @JsonProperty("keyword_match")
    boolean keywordMatch;

    @JsonProperty("keyword_reason")
    String keywordReason;

    double confidence;

    // Méthode explicite pour Spring
    public boolean isToxic() {
        return toxic;
    }
}