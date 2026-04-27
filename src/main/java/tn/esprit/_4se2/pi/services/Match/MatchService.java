package tn.esprit._4se2.pi.services.Match;

import org.springframework.stereotype.Service;
import tn.esprit._4se2.pi.entities.Match;
import tn.esprit._4se2.pi.repositories.MatchRepository;

import java.util.List;

@Service
public class MatchService implements MatchServiceInterface {
    private final MatchRepository matchRepository;

    public MatchService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public Match create(Match match) {
        return matchRepository.save(match);
    }

    public Match update(Long id, Match updated) {
        Match existing = getById(id);
        existing.setVenue(updated.getVenue());
        existing.setScheduledAt(updated.getScheduledAt());
        existing.setStatus(updated.getStatus());
        existing.setHomeScore(updated.getHomeScore());
        existing.setAwayScore(updated.getAwayScore());
        return matchRepository.save(existing);
    }

    public List<Match> getAll(Long competitionId) {
        return matchRepository.findByCompetitionId(competitionId);
    }

    public Match getById(Long id) {
        return matchRepository.findById(id).orElseThrow(() -> new RuntimeException("Match not found"));
    }

    public void delete(Long id) {
        matchRepository.deleteById(id);
    }

    @Override
    public List<tn.esprit._4se2.pi.dto.Match.MatchDTOs.MatchResponseDTO> search(String keyword) {
        return matchRepository.searchMatchesDetailed(keyword);
    }

    @Override
    public List<Match> getHighScoringMatches() {
        return matchRepository.findHighScoringMatches();
    }
}
