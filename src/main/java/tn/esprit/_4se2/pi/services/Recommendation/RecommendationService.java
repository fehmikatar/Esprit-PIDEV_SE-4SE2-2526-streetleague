package tn.esprit._4se2.pi.services.Recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.Enum.RecommendationType;
import tn.esprit._4se2.pi.entities.Category;
import tn.esprit._4se2.pi.entities.Like;
import tn.esprit._4se2.pi.entities.Post;
import tn.esprit._4se2.pi.entities.RecommendationScore;
import tn.esprit._4se2.pi.entities.Team;
import tn.esprit._4se2.pi.entities.User;
import tn.esprit._4se2.pi.repositories.CategoryRepository;
import tn.esprit._4se2.pi.repositories.PostLikeRepository;
import tn.esprit._4se2.pi.repositories.PostRepository;
import tn.esprit._4se2.pi.repositories.RecommendationScoreRepository;
import tn.esprit._4se2.pi.repositories.TeamMemberRepository;
import tn.esprit._4se2.pi.repositories.TeamRepository;
import tn.esprit._4se2.pi.repositories.UserRepository;

import java.text.Normalizer;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RecommendationService {

    private final TeamRepository teamRepository;
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final RecommendationScoreRepository recommendationScoreRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final PostLikeRepository postLikeRepository;

    public List<Team> getSimilarTeams(Long teamId, int limit) {
        Team referenceTeam = teamRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found"));

        int safeLimit = Math.max(1, limit);
        return teamRepository.findAll().stream()
                .filter(team -> !Objects.equals(team.getId(), teamId))
                .map(team -> new AbstractMap.SimpleEntry<>(team, calculateTeamSimilarity(referenceTeam, team)))
                .filter(entry -> entry.getValue() > 0.3)
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(safeLimit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private double calculateTeamSimilarity(Team t1, Team t2) {
        double score = 0.0;
        int criteriaCount = 0;

        if (t1.getCategory() != null && t2.getCategory() != null) {
            if (Objects.equals(t1.getCategory().getId(), t2.getCategory().getId())) {
                score += 0.4;
            } else {
                score += calculateCategorySimilarity(t1.getCategory(), t2.getCategory()) * 0.4;
            }
            criteriaCount++;
        }

        if (t1.getSport() != null && t2.getSport() != null) {
            score += (t1.getSport() == t2.getSport()) ? 0.3 : 0.0;
        }

        if (t1.getLevel() != null && t2.getLevel() != null) {
            score += calculateLevelSimilarity(t1.getLevel(), t2.getLevel());
            criteriaCount++;
        }

        if (t1.getCity() != null && t2.getCity() != null) {
            score += t1.getCity().equalsIgnoreCase(t2.getCity()) ? 0.1 : 0.0;
            criteriaCount++;
        }

        return criteriaCount > 0 ? score : 0.0;
    }

    private double calculateCategorySimilarity(Category cat1, Category cat2) {
        if (Objects.equals(cat1.getId(), cat2.getId())) {
            return 1.0;
        }

        if (cat1.getParentCategory() != null && Objects.equals(cat1.getParentCategory().getId(), cat2.getId())) {
            return 0.7;
        }
        if (cat2.getParentCategory() != null && Objects.equals(cat2.getParentCategory().getId(), cat1.getId())) {
            return 0.7;
        }

        Set<Long> ancestors1 = getAllAncestorIds(cat1);
        Set<Long> ancestors2 = getAllAncestorIds(cat2);
        ancestors1.retainAll(ancestors2);

        return ancestors1.isEmpty() ? 0.2 : 0.5;
    }

    private Set<Long> getAllAncestorIds(Category category) {
        Set<Long> ancestors = new HashSet<>();
        Category current = category.getParentCategory();
        while (current != null) {
            ancestors.add(current.getId());
            current = current.getParentCategory();
        }
        return ancestors;
    }

    private double calculateLevelSimilarity(tn.esprit._4se2.pi.entities.enums.SkillLevel l1, tn.esprit._4se2.pi.entities.enums.SkillLevel l2) {
        int diff = Math.abs(l1.ordinal() - l2.ordinal());
        if (diff == 0) return 0.2;
        if (diff == 1) return 0.1;
        return 0.0;
    }

    public List<Post> getRecommendedPostsForUser(Long userId, int limit) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Set<Long> userCategories = getUserCategoryIds(userId);
        Set<Long> likedPostIds = getLikedPostIdsByUser(userId);
        Map<String, Integer> preferredKeywords = extractKeywordsFromLikedPosts(userId);

        int safeLimit = Math.max(1, limit);
        return postRepository.findAll().stream()
                .filter(post -> !likedPostIds.contains(post.getId()))
                .map(post -> new AbstractMap.SimpleEntry<>(post,
                        calculatePostRelevanceScore(post, userCategories, preferredKeywords)))
                .filter(entry -> entry.getValue() > 0.2)
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(safeLimit)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private double calculatePostRelevanceScore(Post post, Set<Long> userCategories,
                                               Map<String, Integer> preferredKeywords) {
        double score = 0.0;

        if (post.getCategory() != null && userCategories.contains(post.getCategory().getId())) {
            score += 0.4;
        } else if (post.getCategory() != null) {
            score += calculateMaxCategorySimilarity(post.getCategory(), userCategories) * 0.3;
        }

        String title = post.getTitle() == null ? "" : post.getTitle();
        String content = post.getContent() == null ? "" : post.getContent();
        String postContent = (title + " " + content).toLowerCase();

        double keywordScore = preferredKeywords.entrySet().stream()
                .mapToDouble(entry -> postContent.contains(entry.getKey()) ? entry.getValue() * 0.1 : 0.0)
                .sum();
        score += Math.min(keywordScore, 0.4);

        int likeCount = post.getLikes() != null ? post.getLikes().size() : 0;
        score += Math.min(likeCount / 100.0, 0.2);

        if (post.getCreatedAt() != null) {
            long daysOld = Duration.between(post.getCreatedAt(), LocalDateTime.now()).toDays();
            if (daysOld <= 7) {
                score += 0.1;
            } else if (daysOld <= 30) {
                score += 0.05;
            }
        }

        return score;
    }

    private Set<Long> getUserCategoryIds(Long userId) {
        return teamMemberRepository.findByUser_Id(userId).stream()
                .map(tm -> tm.getTeam() != null ? tm.getTeam().getCategory() : null)
                .filter(Objects::nonNull)
                .map(Category::getId)
                .collect(Collectors.toSet());
    }

    private Set<Long> getLikedPostIdsByUser(Long userId) {
        return postLikeRepository.findByUser_Id(userId).stream()
                .map(Like::getPost)
                .filter(Objects::nonNull)
                .map(Post::getId)
                .collect(Collectors.toSet());
    }

    private Map<String, Integer> extractKeywordsFromLikedPosts(Long userId) {
        Map<String, Integer> keywords = new HashMap<>();

        List<Post> likedPosts = postLikeRepository.findByUser_Id(userId).stream()
                .map(Like::getPost)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        for (Post post : likedPosts) {
            String title = post.getTitle() == null ? "" : post.getTitle();
            String content = post.getContent() == null ? "" : post.getContent();
            String text = normalize(title + " " + content);
            String[] words = text.split("\\s+");
            for (String word : words) {
                if (word.length() > 4 && !isStopWord(word)) {
                    keywords.merge(word, 1, Integer::sum);
                }
            }
        }

        return keywords;
    }

    private boolean isStopWord(String word) {
        Set<String> stopWords = Set.of(
                "pour", "avec", "sans", "dans", "mais", "donc", "comme", "entre", "chez", "car"
        );
        return stopWords.contains(word);
    }

    private double calculateMaxCategorySimilarity(Category postCategory, Set<Long> userCategories) {
        double maxSimilarity = 0.0;
        for (Long userCatId : userCategories) {
            Optional<Category> userCategory = categoryRepository.findById(userCatId);
            if (userCategory.isPresent()) {
                maxSimilarity = Math.max(maxSimilarity,
                        calculateCategorySimilarity(postCategory, userCategory.get()));
            }
        }
        return maxSimilarity;
    }

    public void updateRecommendationScores() {
        log.info("Starting recommendation scores update...");
        updateTeamSimilarityScores();
        updatePostRecommendationScores();
        log.info("Recommendation scores update completed");
    }

    private void updateTeamSimilarityScores() {
        List<Team> allTeams = teamRepository.findAll();

        for (int i = 0; i < allTeams.size(); i++) {
            for (int j = i + 1; j < allTeams.size(); j++) {
                Team t1 = allTeams.get(i);
                Team t2 = allTeams.get(j);

                double similarity = calculateTeamSimilarity(t1, t2);

                saveRecommendationScore(RecommendationType.TEAM_TO_TEAM,
                        "TEAM", t1.getId(), "TEAM", t2.getId(), similarity);
                saveRecommendationScore(RecommendationType.TEAM_TO_TEAM,
                        "TEAM", t2.getId(), "TEAM", t1.getId(), similarity);
            }
        }
    }

    private void updatePostRecommendationScores() {
        List<User> allUsers = userRepository.findAll();
        List<Post> allPosts = postRepository.findAll();

        for (User user : allUsers) {
            Set<Long> userCategories = getUserCategoryIds(user.getId());
            Map<String, Integer> preferredKeywords = extractKeywordsFromLikedPosts(user.getId());

            for (Post post : allPosts) {
                double score = calculatePostRelevanceScore(post, userCategories, preferredKeywords);
                saveRecommendationScore(RecommendationType.POST_TO_USER,
                        "USER", user.getId(), "POST", post.getId(), score);
            }
        }
    }

    private void saveRecommendationScore(RecommendationType type, String targetType,
                                         Long targetId, String sourceType,
                                         Long sourceId, double score) {
        RecommendationScore recScore = recommendationScoreRepository
                .findByTargetTypeAndTargetIdAndSourceTypeAndSourceId(targetType, targetId, sourceType, sourceId)
                .orElseGet(RecommendationScore::new);

        recScore.setRecommendationType(type);
        recScore.setTargetType(targetType);
        recScore.setTargetId(targetId);
        recScore.setSourceType(sourceType);
        recScore.setSourceId(sourceId);
        recScore.setSimilarityScore(score);
        recScore.setCalculatedAt(LocalDateTime.now());
        recScore.setActive(true);

        recommendationScoreRepository.save(recScore);
    }

    private String normalize(String value) {
        String normalized = Normalizer.normalize(value == null ? "" : value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return normalized.toLowerCase();
    }
}