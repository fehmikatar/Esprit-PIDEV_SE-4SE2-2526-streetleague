package tn.esprit._4se2.pi.Services;

import tn.esprit._4se2.pi.Entities.Match;

import java.util.List;

public interface MatchServiceInterface {
    Match create(Match match);
    Match update(Long id, Match updated);
    List<Match> getAll(Long competitionId);
    Match getById(Long id);
    void delete(Long id);
}

