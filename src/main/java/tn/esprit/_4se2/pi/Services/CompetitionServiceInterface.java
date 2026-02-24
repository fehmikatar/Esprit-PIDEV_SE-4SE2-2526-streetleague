package tn.esprit._4se2.pi.Services;

import tn.esprit._4se2.pi.Entities.Competition;

import java.util.List;

public interface CompetitionServiceInterface {
    Competition create(Competition competition);
    Competition update(Long id, Competition competition);
    List<Competition> getAll();
    Competition getById(Long id);
    void delete(Long id);
}
