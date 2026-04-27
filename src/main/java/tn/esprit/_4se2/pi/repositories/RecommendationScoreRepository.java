package tn.esprit._4se2.pi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit._4se2.pi.entities.RecommendationScore;

import java.util.Optional;

public interface RecommendationScoreRepository extends JpaRepository<RecommendationScore, Long> {

    Optional<RecommendationScore> findByTargetTypeAndTargetIdAndSourceTypeAndSourceId(
            String targetType,
            Long targetId,
            String sourceType,
            Long sourceId
    );
}