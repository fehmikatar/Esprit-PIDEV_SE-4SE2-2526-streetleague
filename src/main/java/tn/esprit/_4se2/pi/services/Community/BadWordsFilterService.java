package tn.esprit._4se2.pi.services.Community;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.regex.*;

@Service
public class BadWordsFilterService {

    private static final List<String> BAD_WORDS = List.of(
        // 🇬🇧 English
        "fuck", "shit", "bitch", "asshole", "bastard", "dick", "pussy",
        "cunt", "motherfucker", "nigger", "faggot", "whore", "slut", "damn",

        // 🇫🇷 Français
        "merde", "putain", "connard", "salope", "enculé", "fils de pute",
        "batard", "bâtard", "nique", "va te faire", "con", "conne",
        "foutre", "chier", "pute", "couille", "bite", "couilles",

        // 🇹🇳 Dialecte tunisien
        "oussou", "zebi", "zeb", "kess", "kess ommek", "ya khawal",
        "barra", "a7a", "a7", "nik", "omek", "rabbik", "ta7an",
        "7arami", "sharmouta", "9a7ba", "9s", "7mar", "7mara",
        "koffet", "bel3a", "wled l9a7ba", "ibn el9a7ba",

        // 🇸🇦 Arabe standard
        "عاهرة", "كس", "زب", "شرموطة", "يلعن", "ابن الكلب",
        "كلب", "حمار", "غبي", "احمق", "لعنة", "ملعون",
        "خول", "منيوك", "طيز", "ابن العاهرة"
    );

    private final Pattern pattern;

    public BadWordsFilterService() {
        String regex = BAD_WORDS.stream()
            .sorted(Comparator.comparingInt(String::length).reversed())
            .map(word -> Pattern.quote(word.toLowerCase()))
            .reduce((a, b) -> a + "|" + b)
            .orElse("(?!)");

        this.pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
    }

    public String filter(String text) {
        if (text == null || text.isBlank()) return text;

        Matcher matcher = pattern.matcher(text);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String match = matcher.group();
            String stars = "*".repeat(match.length());
            matcher.appendReplacement(result, Matcher.quoteReplacement(stars));
        }

        matcher.appendTail(result);
        return result.toString();
    }

    public boolean containsBadWords(String text) {
        if (text == null || text.isBlank()) return false;
        return pattern.matcher(text).find();
    }
}