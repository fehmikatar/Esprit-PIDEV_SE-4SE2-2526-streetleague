package tn.esprit._4se2.pi.Services;

import org.springframework.stereotype.Service;
import tn.esprit._4se2.pi.Entities.Match;
import tn.esprit._4se2.pi.Repositories.MatchRepository;

import java.util.List;

@Service
public class MatchService {
    private final MatchRepository matchRepository;

    public MatchService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    public Match create(Match match) {
        return matchRepository.save(match);
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
}
