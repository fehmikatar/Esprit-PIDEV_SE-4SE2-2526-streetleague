package tn.esprit._4se2.pi.services.Matching;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit._4se2.pi.dto.Matching.MatchingDTOs.*;
import tn.esprit._4se2.pi.repositories.TeamRepository;
import tn.esprit._4se2.pi.repositories.PlayerRepository;
import tn.esprit._4se2.pi.entities.Team;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final TeamRepository teamRepository;
    private final PlayerRepository playerRepository;

    /**
     * Recommande les meilleures équipes pour un profil de joueur donné.
     */
    public List<MatchResponse> getBestTeamsForProfile(PlayerProfileMatchingRequest request, int limit) {
        List<Team> allTeams = teamRepository.findAll();
        
        return allTeams.stream()
            .filter(t -> t.getSport() != null && t.getSport().equalsIgnoreCase(request.getSportType()))
            .map(t -> {
                double score = 60.0; // Base score pour le même sport
                String details = "Match basé sur le sport " + t.getSport();
                
                // Bonus pour la même ville
                if (t.getCity() != null && t.getCity().equalsIgnoreCase(request.getCity())) {
                    score += 30.0;
                    details += " et la ville (" + t.getCity() + ")";
                }
                
                // Bonus pour le niveau (Beginner/Intermediate/etc)
                if (t.getLevel() != null && t.getLevel().equalsIgnoreCase(request.getSkillLevel())) {
                    score += 10.0;
                    details += ". Niveau correspondant.";
                }
                
                // On ajoute un peu de random pour la variété si les scores sont égaux
                score += (Math.random() * 5.0);
                
                return new MatchResponse(
                    t.getId(),
                    t.getName(),
                    Math.min(100.0, score),
                    details,
                    2.5 // Distance simulée
                );
            })
            .sorted((a, b) -> b.getScore().compareTo(a.getScore()))
            .limit(limit)
            .collect(Collectors.toList());
    }

    public List<MatchResponse> getBestTeamsForPlayer(Long playerId, int limit) {
        // Pour l'instant, on redirige vers une recherche générique vide
        return getBestTeamsForProfile(new PlayerProfileMatchingRequest(), limit);
    }

    public List<MatchResponse> getBestPlayersForTeam(Long teamId, int limit) {
        // Simulation pour l'instant
        return new ArrayList<>();
    }
}
