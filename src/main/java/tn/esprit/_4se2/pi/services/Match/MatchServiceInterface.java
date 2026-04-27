package tn.esprit._4se2.pi.services.Match;

import tn.esprit._4se2.pi.entities.Match;

import java.util.List;

public interface MatchServiceInterface {
    Match create(Match match);
    Match update(Long id, Match updated);
    List<Match> getAll(Long competitionId);
    Match getById(Long id);
    void delete(Long id);
    List<tn.esprit._4se2.pi.dto.Match.MatchDTOs.MatchResponseDTO> search(String keyword);
    List<Match> getHighScoringMatches();
}

