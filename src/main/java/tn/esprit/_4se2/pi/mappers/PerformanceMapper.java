package tn.esprit._4se2.pi.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.dto.PerformanceRequest;
import tn.esprit._4se2.pi.dto.PerformanceResponse;
import tn.esprit._4se2.pi.entities.Performance;
import tn.esprit._4se2.pi.entities.Player;
import tn.esprit._4se2.pi.repositories.PlayerRepository;
import tn.esprit._4se2.pi.exception.ResourceNotFoundException;

@Component
@RequiredArgsConstructor
public class PerformanceMapper {

    private final PlayerRepository playerRepository;

    public Performance toEntity(PerformanceRequest request) {
        Player player = playerRepository.findById(request.getPlayerId())
                .orElseThrow(() -> new ResourceNotFoundException("Joueur non trouvé id: " + request.getPlayerId()));

        Performance performance = new Performance();
        performance.setPlayer(player);
        performance.setMatchId(request.getMatchId());
        performance.setScore(request.getScore());
        performance.setAssists(request.getAssists());
        performance.setDistanceCovered(request.getDistanceCovered());
        performance.setTimePlayed(request.getTimePlayed());
        performance.setRating(request.getRating());
        return performance;
    }

    public PerformanceResponse toResponse(Performance performance) {
        return PerformanceResponse.builder()
                .id(performance.getId())
                .playerId(performance.getPlayer().getId())
                .matchId(performance.getMatchId())
                .score(performance.getScore())
                .assists(performance.getAssists())
                .distanceCovered(performance.getDistanceCovered())
                .timePlayed(performance.getTimePlayed())
                .rating(performance.getRating())
                .build();
    }

    public void updateEntity(PerformanceRequest request, Performance performance) {
        // On ne met à jour que les champs modifiables
        performance.setScore(request.getScore());
        performance.setAssists(request.getAssists());
        performance.setDistanceCovered(request.getDistanceCovered());
        performance.setTimePlayed(request.getTimePlayed());
        performance.setRating(request.getRating());
        // Si le joueur ou le matchId changent, cela pourrait être géré mais on suppose fixes ici
    }
}