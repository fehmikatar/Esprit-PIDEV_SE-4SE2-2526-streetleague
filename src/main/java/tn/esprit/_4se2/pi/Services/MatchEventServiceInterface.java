package tn.esprit._4se2.pi.Services;

import tn.esprit._4se2.pi.Entities.MatchEvent;

import java.util.List;

public interface MatchEventServiceInterface {

        MatchEvent logEvent(MatchEvent matchEvent);
        List<MatchEvent> getByMatchId(Long matchId);
        void delete(Long id);
    }