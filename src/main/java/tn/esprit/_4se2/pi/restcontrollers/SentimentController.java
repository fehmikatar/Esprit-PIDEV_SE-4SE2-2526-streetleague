package tn.esprit._4se2.pi.restcontrollers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit._4se2.pi.dto.Sentiment.SentimentDTOs;
import tn.esprit._4se2.pi.services.Sentiment.CommunitySentimentTrend;
import tn.esprit._4se2.pi.services.Sentiment.SentimentAnalysisService;
import tn.esprit._4se2.pi.services.Sentiment.SentimentResult;

@RestController
@RequestMapping("/api/sentiment")
@RequiredArgsConstructor
public class SentimentController {

    private final SentimentAnalysisService sentimentAnalysisService;

    @PostMapping("/analyze")
    public ResponseEntity<SentimentDTOs.SentimentResponse> analyzeText(
            @Valid @RequestBody SentimentDTOs.AnalyzeTextRequest request
    ) {
        SentimentResult result = sentimentAnalysisService.analyzeText(request.getText());
        return ResponseEntity.ok(toSentimentResponse(result));
    }

    @GetMapping("/communities/{communityId}/trend")
    public ResponseEntity<SentimentDTOs.CommunityTrendResponse> analyzeCommunityTrend(
            @PathVariable Long communityId,
            @RequestParam(defaultValue = "30") int days
    ) {
        CommunitySentimentTrend trend = sentimentAnalysisService.analyzeCommunityTrend(communityId, days);
        return ResponseEntity.ok(toTrendResponse(trend));
    }

    private SentimentDTOs.SentimentResponse toSentimentResponse(SentimentResult result) {
        return SentimentDTOs.SentimentResponse.builder()
                .sentiment(result.getSentiment())
                .score(result.getScore())
                .intensity(result.getIntensity())
                .aspects(result.getAspects())
                .build();
    }

    private SentimentDTOs.CommunityTrendResponse toTrendResponse(CommunitySentimentTrend trend) {
        return SentimentDTOs.CommunityTrendResponse.builder()
                .communityId(trend.getCommunityId())
                .averageSentiment(trend.getAverageSentiment())
                .trend(trend.getTrend())
                .aspectAverages(trend.getAspectAverages())
                .periodDays(trend.getPeriodDays())
                .build();
    }
}