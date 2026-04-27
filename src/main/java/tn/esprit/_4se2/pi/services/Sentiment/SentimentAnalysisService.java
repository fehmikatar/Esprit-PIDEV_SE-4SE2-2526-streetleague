package tn.esprit._4se2.pi.services.Sentiment;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit._4se2.pi.entities.Post;
import tn.esprit._4se2.pi.repositories.PostRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SentimentAnalysisService {

    private StanfordCoreNLP pipeline;
    private final Map<String, SentimentResult> cache = new ConcurrentHashMap<>();
    private final PostRepository postRepository;

    private static final Map<String, Double> SPORT_LEXICON = Map.ofEntries(
            Map.entry("genial", 0.9), Map.entry("incroyable", 0.9), Map.entry("super", 0.8),
            Map.entry("excellent", 0.9), Map.entry("parfait", 0.8), Map.entry("bien", 0.6),
            Map.entry("moyen", 0.0), Map.entry("decevant", -0.6), Map.entry("nul", -0.8),
            Map.entry("horrible", -0.9), Map.entry("catastrophique", -0.9), Map.entry("victoire", 0.7),
            Map.entry("defaite", -0.5), Map.entry("champion", 0.8), Map.entry("perdant", -0.6),
            Map.entry("blessure", -0.4), Map.entry("performance", 0.3), Map.entry("entrainement", 0.2)
    );

    @PostConstruct
    public void init() {
        try {
            Properties props = new Properties();
            props.setProperty("annotators", "tokenize,ssplit,pos,lemma,parse,sentiment");
            props.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
            this.pipeline = new StanfordCoreNLP(props);
            log.info("Sentiment analysis pipeline initialized");
        } catch (Exception e) {
            log.warn("Failed to initialize Stanford CoreNLP models. Sentiment analysis will be disabled: {}", e.getMessage());
            this.pipeline = null;
        }
    }

    public SentimentResult analyzeText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new SentimentResult("NEUTRAL", 0.0, "EMPTY");
        }

        if (pipeline == null) {
            log.debug("Stanford CoreNLP pipeline not available, using lexicon-based analysis only");
            double lexiconScore = analyzeWithLexicon(text);
            String dominantSentiment = getSentimentLabel(lexiconScore);
            return new SentimentResult(dominantSentiment, lexiconScore, "LEXICON_ONLY");
        }

        String cacheKey = text.hashCode() + "_" + text.length();
        SentimentResult cached = cache.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        Annotation annotation = new Annotation(text);
        pipeline.annotate(annotation);

        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);

        double totalSentiment = 0.0;
        int sentenceCount = 0;

        for (CoreMap sentence : sentences) {
            String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
            double score = convertSentimentToScore(sentiment);

            double lexiconScore = analyzeWithLexicon(sentence.toString());
            score = (score + lexiconScore) / 2;

            totalSentiment += score;
            sentenceCount++;
        }

        double averageScore = sentenceCount > 0 ? totalSentiment / sentenceCount : 0.0;
        String dominantSentiment = getSentimentLabel(averageScore);

        boolean isSarcastic = detectSarcasm(text);
        boolean hasEmotionalIntensity = detectEmotionalIntensity(text);

        SentimentResult result = new SentimentResult(
                dominantSentiment,
                averageScore,
                hasEmotionalIntensity ? "HIGH_INTENSITY" : (isSarcastic ? "SARCASTIC" : "NORMAL"),
                analyzeAspects(text)
        );

        cache.put(cacheKey, result);
        return result;
    }

    private double analyzeWithLexicon(String text) {
        String lowerText = normalize(text);
        double score = 0.0;
        int matches = 0;

        for (Map.Entry<String, Double> entry : SPORT_LEXICON.entrySet()) {
            if (lowerText.contains(entry.getKey())) {
                score += entry.getValue();
                matches++;
            }
        }

        if (containsNegation(lowerText)) {
            score = -score;
        }

        if (containsIntensifier(lowerText)) {
            score = score * 1.5;
        }

        return matches > 0 ? score / matches : 0.0;
    }

    private boolean containsNegation(String text) {
        String[] negations = {"ne", "pas", "jamais", "rien", "aucun", "ni", "sans"};
        for (String neg : negations) {
            if (text.contains(neg)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsIntensifier(String text) {
        String[] intensifiers = {"tres", "vraiment", "extremement", "tellement", "super", "hyper"};
        for (String intr : intensifiers) {
            if (text.contains(intr)) {
                return true;
            }
        }
        return false;
    }

    private boolean detectSarcasm(String text) {
        String lowerText = normalize(text);

        boolean hasPositiveWord = SPORT_LEXICON.entrySet().stream()
                .anyMatch(entry -> lowerText.contains(entry.getKey()) && entry.getValue() > 0.5);

        boolean hasNegativeContext = lowerText.contains("mais")
                || lowerText.contains("cependant")
                || lowerText.contains("dommage")
                || (text.contains("!") && text.toUpperCase().equals(text));

        return hasPositiveWord && hasNegativeContext;
    }

    private boolean detectEmotionalIntensity(String text) {
        int exclamationCount = text.length() - text.replace("!", "").length();

        int uppercaseWords = 0;
        String[] words = text.split("\\s+");
        for (String word : words) {
            if (word.length() > 2 && word.toUpperCase().equals(word) && !word.matches(".*\\d.*")) {
                uppercaseWords++;
            }
        }

        return exclamationCount >= 2 || uppercaseWords >= 2;
    }

    private Map<String, Double> analyzeAspects(String text) {
        Map<String, Double> aspects = new HashMap<>();
        String lowerText = normalize(text);

        String[] teamAspects = {"equipe", "team", "collectif", "groupe"};
        String[] playerAspects = {"joueur", "player", "athlete", "sportif"};
        String[] matchAspects = {"match", "partie", "rencontre", "competition"};
        String[] trainingAspects = {"entrainement", "training", "preparation", "exercice"};
        String[] coachAspects = {"coach", "entraineur", "manager"};

        aspects.put("TEAM", calculateAspectScore(lowerText, teamAspects));
        aspects.put("PLAYER", calculateAspectScore(lowerText, playerAspects));
        aspects.put("MATCH", calculateAspectScore(lowerText, matchAspects));
        aspects.put("TRAINING", calculateAspectScore(lowerText, trainingAspects));
        aspects.put("COACH", calculateAspectScore(lowerText, coachAspects));

        return aspects;
    }

    private double calculateAspectScore(String text, String[] keywords) {
        double totalScore = 0.0;
        int mentions = 0;

        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                mentions++;
                int index = text.indexOf(keyword);
                int start = Math.max(0, index - 50);
                int end = Math.min(text.length(), index + 50);
                String context = text.substring(start, end);
                totalScore += analyzeWithLexicon(context);
            }
        }

        return mentions > 0 ? totalScore / mentions : 0.0;
    }

    private double convertSentimentToScore(String sentiment) {
        return switch (sentiment) {
            case "Very negative" -> -0.8;
            case "Negative" -> -0.4;
            case "Neutral" -> 0.0;
            case "Positive" -> 0.5;
            case "Very positive" -> 0.9;
            default -> 0.0;
        };
    }

    private String getSentimentLabel(double score) {
        if (score <= -0.6) {
            return "VERY_NEGATIVE";
        }
        if (score <= -0.2) {
            return "NEGATIVE";
        }
        if (score < 0.2) {
            return "NEUTRAL";
        }
        if (score < 0.6) {
            return "POSITIVE";
        }
        return "VERY_POSITIVE";
    }

    public CommunitySentimentTrend analyzeCommunityTrend(Long communityId, int days) {
        int safeDays = Math.max(1, days);
        LocalDateTime since = LocalDateTime.now().minusDays(safeDays);
        List<Post> posts = postRepository.findByCommunity_IdAndCreatedAtAfter(communityId, since);

        List<Double> dailyScores = new ArrayList<>();
        Map<String, List<Double>> aspectTrends = new HashMap<>();

        for (Post post : posts) {
            String title = post.getTitle() == null ? "" : post.getTitle();
            String content = post.getContent() == null ? "" : post.getContent();
            SentimentResult result = analyzeText(title + " " + content);
            dailyScores.add(result.getScore());

            result.getAspects().forEach((aspect, score) ->
                    aspectTrends.computeIfAbsent(aspect, k -> new ArrayList<>()).add(score));
        }

        double averageSentiment = dailyScores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
        double trend = calculateTrend(dailyScores);

        return CommunitySentimentTrend.builder()
                .communityId(communityId)
                .averageSentiment(averageSentiment)
                .trend(trend)
                .aspectAverages(aspectTrends.entrySet().stream().collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue().stream().mapToDouble(Double::doubleValue).average().orElse(0.0)
                )))
                .periodDays(safeDays)
                .build();
    }

    private double calculateTrend(List<Double> scores) {
        if (scores.size() < 2) {
            return 0.0;
        }

        int n = scores.size();
        double sumX = 0;
        double sumY = 0;
        double sumXY = 0;
        double sumX2 = 0;

        for (int i = 0; i < n; i++) {
            sumX += i;
            sumY += scores.get(i);
            sumXY += i * scores.get(i);
            sumX2 += i * i;
        }

        double denominator = n * sumX2 - sumX * sumX;
        if (denominator == 0) {
            return 0.0;
        }
        return (n * sumXY - sumX * sumY) / denominator;
    }

    private String normalize(String value) {
        return value
                .toLowerCase()
                .replace('é', 'e')
                .replace('è', 'e')
                .replace('ê', 'e')
                .replace('à', 'a')
                .replace('â', 'a')
                .replace('ù', 'u')
                .replace('û', 'u')
                .replace('î', 'i')
                .replace('ï', 'i')
                .replace('ô', 'o')
                .replace('ç', 'c');
    }
}