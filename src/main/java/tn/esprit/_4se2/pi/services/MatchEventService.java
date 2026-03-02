package tn.esprit._4se2.pi.services;

import org.springframework.stereotype.Service;
import tn.esprit._4se2.pi.entities.MatchEvent;
import tn.esprit._4se2.pi.repositories.MatchEventRepository;

import java.util.List;

@Service
public class MatchEventService {
    private final MatchEventRepository matchEventRepository;

    public MatchEventService(MatchEventRepository matchEventRepository) {
        this.matchEventRepository = matchEventRepository;
    }

    public MatchEvent logEvent(MatchEvent matchEvent) {
        return matchEventRepository.save(matchEvent);
    }

    public List<MatchEvent> getByMatchId(Long matchId) {
        return matchEventRepository.findByMatchId(matchId);
    }

    public void delete(Long id) {
        matchEventRepository.deleteById(id);
    }
}
