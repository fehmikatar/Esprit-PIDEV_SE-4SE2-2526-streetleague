package tn.esprit._4se2.pi.restcontrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit._4se2.pi.dto.Recommendation.RecommendationDTO;
import tn.esprit._4se2.pi.entities.Post;
import tn.esprit._4se2.pi.entities.Team;
import tn.esprit._4se2.pi.services.Recommendation.RecommendationService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationService recommendationService;

    @GetMapping("/teams/{teamId}/similar")
    public ResponseEntity<List<RecommendationDTO.TeamRecommendationResponse>> getSimilarTeams(
            @PathVariable Long teamId,
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<RecommendationDTO.TeamRecommendationResponse> response = recommendationService
                .getSimilarTeams(teamId, limit)
                .stream()
                .map(this::toTeamResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userId}/posts")
    public ResponseEntity<List<RecommendationDTO.PostRecommendationResponse>> getRecommendedPostsForUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<RecommendationDTO.PostRecommendationResponse> response = recommendationService
                .getRecommendedPostsForUser(userId, limit)
                .stream()
                .map(this::toPostResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/scores/update")
    public ResponseEntity<String> updateRecommendationScores() {
        recommendationService.updateRecommendationScores();
        return ResponseEntity.ok("Recommendation scores updated successfully");
    }

    private RecommendationDTO.TeamRecommendationResponse toTeamResponse(Team team) {
        return RecommendationDTO.TeamRecommendationResponse.builder()
                .teamId(team.getId())
                .teamName(team.getName())
                .sport(team.getSport() != null ? team.getSport().name() : "")
                .level(team.getLevel() != null ? team.getLevel().name() : "")
                .city(team.getCity())
                .categoryId(team.getCategory() != null ? team.getCategory().getId() : null)
                .categoryName(team.getCategory() != null ? team.getCategory().getNom() : null)
                .build();
    }

    private RecommendationDTO.PostRecommendationResponse toPostResponse(Post post) {
        return RecommendationDTO.PostRecommendationResponse.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .authorId(post.getAuthor() != null ? post.getAuthor().getId() : null)
                .authorFirstName(post.getAuthor() != null ? post.getAuthor().getFirstName() : null)
                .authorLastName(post.getAuthor() != null ? post.getAuthor().getLastName() : null)
                .categoryId(post.getCategory() != null ? post.getCategory().getId() : null)
                .categoryName(post.getCategory() != null ? post.getCategory().getNom() : null)
                .communityId(post.getCommunity() != null ? post.getCommunity().getId() : null)
                .communityName(post.getCommunity() != null ? post.getCommunity().getName() : null)
                .createdAt(post.getCreatedAt())
                .build();
    }
}