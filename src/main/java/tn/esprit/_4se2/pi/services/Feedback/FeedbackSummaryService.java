package tn.esprit._4se2.pi.services.Feedback;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackSummaryService {

    private static final String SYSTEM_PROMPT = "Tu es un assistant qui analyse les avis d'un terrain de sport. Fais un résumé court et objectif en 3-4 phrases des avis suivants. Mentionne les points positifs et négatifs si présents.";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${groq.api.url:https://api.groq.com/openai/v1/chat/completions}")
    private String groqApiUrl;

    @Value("${groq.api.key:}")
    private String groqApiKey;

    @Value("${groq.api.model:llama-3.3-70b-versatile}")
    private String groqModel;

    public String summarizeComments(List<String> comments) {
        List<String> normalizedComments = comments == null
                ? List.of()
                : comments.stream()
                .filter(comment -> comment != null && !comment.isBlank())
                .map(String::trim)
                .toList();

        if (normalizedComments.isEmpty()) {
            throw new IllegalArgumentException("Aucun commentaire à résumer.");
        }

        if (groqApiKey == null || groqApiKey.isBlank()) {
            throw new RuntimeException("Clé Groq manquante côté backend.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(groqApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "model", groqModel,
                "temperature", 0.3,
                "max_tokens", 220,
                "messages", List.of(
                        Map.of("role", "system", "content", SYSTEM_PROMPT),
                        Map.of("role", "user", "content", String.join("\n\n", normalizedComments))
                )
        );

        try {
            JsonNode response = restTemplate.postForObject(
                    groqApiUrl,
                    new HttpEntity<>(body, headers),
                    JsonNode.class
            );

            String summary = response == null
                    ? ""
                    : response.path("choices").path(0).path("message").path("content").asText("").trim();

            if (summary.isBlank()) {
                throw new RuntimeException("Aucun résumé n'a été généré par l'IA.");
            }

            return summary;
        } catch (RestClientResponseException exception) {
            String providerMessage = extractProviderErrorMessage(exception.getResponseBodyAsString());
            log.error(
                    "Erreur lors de l'appel Groq pour le résumé des avis. Status: {}, Body: {}",
                    exception.getStatusCode(),
                    exception.getResponseBodyAsString(),
                    exception
            );

            throw new RuntimeException(
                    providerMessage.isBlank()
                            ? "Erreur Groq lors de l'analyse des avis."
                            : providerMessage
            );
        } catch (RestClientException exception) {
            log.error("Erreur lors de l'appel Groq pour le résumé des avis", exception);
            throw new RuntimeException(
                    exception.getMessage() == null || exception.getMessage().isBlank()
                            ? "Erreur lors de l'analyse des avis par l'IA."
                            : exception.getMessage()
            );
        }
    }

    private String extractProviderErrorMessage(String responseBody) {
        if (responseBody == null || responseBody.isBlank()) {
            return "";
        }

        try {
            JsonNode json = objectMapper.readTree(responseBody);
            return json.path("error").path("message").asText("").trim();
        } catch (Exception exception) {
            return responseBody.trim();
        }
    }
}
