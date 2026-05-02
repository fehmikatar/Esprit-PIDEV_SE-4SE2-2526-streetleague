package tn.esprit._4se2.pi.services.Community;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
public class BadWordsFilterService {

    private static final List<String> BAD_WORDS = List.of(
        // English
        "fuck", "fucking", "fucker", "shit", "shitty", "ass", "asshole",
        "bitch", "bastard", "damn", "crap", "dick", "pussy", "cock",
        "bullshit", "motherfucker", "cunt", "whore", "slut", "faggot",
        "nigger", "nigga", "retard", "kill yourself",
        // French
        "merde", "putain", "connard", "connasse", "salope", "enculé",
        "enculer", "encule", "con", "chier", "foutre", "baiser", "bite",
        "couilles", "nique", "niquer", "pute", "fdp", "pd",
        "débile", "abruti", "imbécile", "crétin", "gros con",
        "va te faire", "fils de pute", "ta gueule"
    );

    public String filterText(String input) {
        if (input == null || input.isBlank()) return input;
        String result = input;
        for (String word : BAD_WORDS) {
            String replacement = "*".repeat(word.length());
            result = result.replaceAll("(?i)" + Pattern.quote(word), replacement);
        }
        return result;
    }
}
