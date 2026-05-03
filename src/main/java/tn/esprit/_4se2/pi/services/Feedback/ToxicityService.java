package tn.esprit._4se2.pi.services.Feedback;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tn.esprit._4se2.pi.dto.Feedback.ToxicityResult;

import java.util.Map;

/**
 * Service qui appelle le serveur FastAPI pour analyser la toxicité d'un texte.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ToxicityService {

    private final RestTemplate restTemplate;

    @Value("${toxicity.api.url:http://localhost:8000}")
    private String apiUrl;

    /**
     * Envoie le texte au serveur FastAPI et retourne le résultat d'analyse.
     *
     * @param text  le commentaire à analyser
     * @return      ToxicityResult avec is_toxic, label, scores, etc.
     */
    public ToxicityResult analyze(String text) {
        String endpoint = apiUrl + "/api/toxicity/analyze";


        try {
            Map<String, String> body = Map.of("text", text);
            ToxicityResult result = restTemplate.postForObject(
                    endpoint, body, ToxicityResult.class
            );
            log.info("[Toxicity] '{}' → {} (hate={}, sent={})",
                    text, result.getLabel(),
                    result.getHateScore(), result.getSentimentScore());
            return result;

        } catch (Exception e) {
            log.error("[Toxicity] Erreur lors de l'appel FastAPI : {}", e.getMessage());
            // En cas d'erreur du serveur IA, on laisse passer (fail-open)
            return ToxicityResult.builder()
                    .text(text)
                    .toxic(false)
                    .label("NORMAL")
                    .confidence(0.0)
                    .build();
        }
    }

    /**
     * Vérifie rapidement si un texte est toxique.
     */
    public boolean isToxic(String text) {
        return analyze(text).isToxic();
    }
}