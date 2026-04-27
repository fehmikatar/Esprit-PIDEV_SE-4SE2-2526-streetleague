package tn.esprit._4se2.pi.services.MatchEvent;

import org.springframework.stereotype.Service;
import tn.esprit._4se2.pi.entities.MatchEvent;
import tn.esprit._4se2.pi.repositories.MatchEventRepository;

import java.util.List;

@Service
public class MatchEventService implements MatchEventServiceInterface {
    private final MatchEventRepository matchEventRepository;

    public MatchEventService(MatchEventRepository matchEventRepository) {
        this.matchEventRepository = matchEventRepository;
    }

    @Override
    public MatchEvent logEvent(MatchEvent matchEvent) {
        return matchEventRepository.save(matchEvent);
    }

    @Override
    public List<MatchEvent> getByMatchId(Long matchId) {
        return matchEventRepository.findByMatchId(matchId);
    }

    @Override
    public void delete(Long id) {
        matchEventRepository.deleteById(id);
    }

    @Override
    public List<MatchEvent> search(String keyword) {
        return matchEventRepository.searchEvents(keyword);
    }
}
