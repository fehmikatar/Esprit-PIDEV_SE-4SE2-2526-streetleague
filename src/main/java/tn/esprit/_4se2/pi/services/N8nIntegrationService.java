package tn.esprit._4se2.pi.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import tn.esprit._4se2.pi.entities.HealthProfile;
import tn.esprit._4se2.pi.entities.Player;
import tn.esprit._4se2.pi.entities.User;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class N8nIntegrationService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${n8n.webhook.url:https://app45.app.n8n.cloud/webhook-test/generate-sports-suggestions}")
    private String webhookUrl;

    @Value("${n8n.webhook.post-suggestion.url:https://app45.app.n8n.cloud/webhook-test/generate-sports-suggestions}")
    private String postSuggestionWebhookUrl;

    // ─────────────────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Calls the n8n webhook to generate a suggested community post for a team.
     */
    public String getPostSuggestion(Long teamId, String teamName, String sport, User user) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("teamId", teamId);
        payload.put("teamName", teamName);
        payload.put("sport", sport);
        payload.put("userId", user.getId());
        payload.put("userName", user.getFirstName() + " " + user.getLastName());
        payload.put("type", "TEAM_POST_SUGGESTION");

        try {
            log.info("[N8n] POST {} — teamId={}, sport={}", postSuggestionWebhookUrl, teamId, sport);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    postSuggestionWebhookUrl, HttpMethod.POST, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                System.out.println("DEBUG: Raw n8n response: " + response.getBody());
                
                // If n8n explicitly reports an error status, do not show it as a suggestion
                if ("error".equals(response.getBody().get("status"))) {
                    log.warn("[N8n] Workflow returned an error status");
                    return "";
                }
                
                // Check for 'suggestion' first, then fallback to 'output' or 'suggestions' (plural)
                Object suggestion = response.getBody().get("suggestion");
                if (suggestion == null) {
                    suggestion = response.getBody().get("suggestions");
                }
                if (suggestion == null) {
                    suggestion = response.getBody().get("output");
                }
                
                if (suggestion != null) {
                    return String.valueOf(suggestion);
                }
            }
            return "";
        } catch (Exception e) {
            log.error("[N8n] Post suggestion failed: {}", e.getMessage());
            return "";
        }
    }

    /**
     * Calls the n8n webhook with an enriched payload built from the user's
     * HealthProfile and Player data.  Returns the list of sport suggestion
     * strings returned by the workflow (e.g. ["Football", "Cycling"]).
     */
    public List<String> getSportsSuggestions(User user,
                                              HealthProfile healthProfile,
                                              Player player) {
        Map<String, Object> payload = buildPayload(user, healthProfile, player);
        return callWebhook(payload);
    }

    /**
     * Convenience overload when no Player/HealthProfile data is available yet.
     * Sends a minimal payload derived from the User entity alone.
     */
    public List<String> getSportsSuggestions(User user) {
        return getSportsSuggestions(user, null, null);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Payload builder
    // ─────────────────────────────────────────────────────────────────────────

    private Map<String, Object> buildPayload(User user,
                                              HealthProfile healthProfile,
                                              Player player) {
        Map<String, Object> payload = new LinkedHashMap<>();

        // ── User basics ──────────────────────────────────────────────────────
        payload.put("userId",    user.getId());
        payload.put("firstName", user.getFirstName());
        payload.put("lastName",  user.getLastName());

        // ── Health profile ───────────────────────────────────────────────────
        if (healthProfile != null) {
            payload.put("age",          healthProfile.getAge());
            payload.put("weight",       healthProfile.getWeight());
            payload.put("height",       healthProfile.getHeight());
            payload.put("bmi",          healthProfile.getBmi());
            payload.put("bmiCategory",  healthProfile.getBmiCategory());
            payload.put("fitnessLevel", healthProfile.getFitnessStatus() != null
                    ? healthProfile.getFitnessStatus().name().toLowerCase()
                    : "unknown");
            payload.put("sportPosition",    healthProfile.getSportPosition());
            payload.put("medicalConditions", healthProfile.getMedicalConditions());
            payload.put("bloodType",         healthProfile.getBloodType());
        } else {
            payload.put("fitnessLevel", "unknown");
        }

        // ── Player stats ─────────────────────────────────────────────────────
        if (player != null) {
            payload.put("skillLevel",  player.getSkillLevel());
            payload.put("position",    player.getPosition());
            payload.put("gamesPlayed", player.getGamesPlayed());
            payload.put("rating",      player.getRating());
        }

        // ── Goal heuristic (derived from fitnessStatus + bmi) ────────────────
        payload.put("goal",        deriveGoal(healthProfile));
        payload.put("preferences", derivePreferences(player, healthProfile));

        log.debug("[N8n] Built payload for userId={}: {}", user.getId(), payload);
        return payload;
    }

    /** Simple heuristic — replace with richer logic as needed. */
    private String deriveGoal(HealthProfile hp) {
        if (hp == null) return "general fitness";
        String bmiCat = hp.getBmiCategory();
        if (bmiCat == null) return "general fitness";
        return switch (bmiCat) {
            case "Surpoids", "Obésité"           -> "lose weight";
            case "Insuffisance pondérale"         -> "gain muscle";
            case "Poids normal"                   -> "maintain fitness";
            default                               -> "general fitness";
        };
    }

    /** Builds a preference list from available profile data. */
    private List<String> derivePreferences(Player player, HealthProfile hp) {
        List<String> prefs = new ArrayList<>();
        if (player != null && player.getPosition() != null) {
            prefs.add("team");
        }
        if (hp != null && hp.getSportPosition() != null) {
            prefs.add("competitive");
        }
        if (prefs.isEmpty()) {
            prefs.add("outdoor");
        }
        return prefs;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HTTP call
    // ─────────────────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private List<String> callWebhook(Map<String, Object> payload) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Uncomment when n8n is secured with a bearer token:
        // headers.setBearerAuth(System.getenv("N8N_WEBHOOK_SECRET"));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        try {
            log.info("[N8n] POST {} — userId={}", webhookUrl, payload.get("userId"));
            ResponseEntity<Map> response = restTemplate.exchange(
                    webhookUrl, HttpMethod.POST, request, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Object raw = response.getBody().get("suggestions");
                if (raw instanceof List<?> list) {
                    List<String> suggestions = new ArrayList<>();
                    for (Object item : list) {
                        suggestions.add(String.valueOf(item));
                    }
                    log.info("[N8n] Received {} suggestion(s) for userId={}", suggestions.size(), payload.get("userId"));
                    return suggestions;
                }
            }

            log.warn("[N8n] Unexpected response body: {}", response.getBody());
            return Collections.emptyList();

        } catch (HttpClientErrorException e) {
            log.error("[N8n] Client error {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("n8n webhook returned client error: " + e.getStatusCode());
        } catch (HttpServerErrorException e) {
            log.error("[N8n] Server error {}: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("n8n workflow failed with server error: " + e.getStatusCode());
        } catch (ResourceAccessException e) {
            log.error("[N8n] Connection timeout/refused: {}", e.getMessage());
            throw new RuntimeException("Could not reach n8n webhook — check connectivity.");
        }
    }
}
