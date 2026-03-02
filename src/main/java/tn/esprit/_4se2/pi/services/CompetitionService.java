package tn.esprit._4se2.pi.services;

import org.springframework.stereotype.Service;
import tn.esprit._4se2.pi.entities.Competition;
import tn.esprit._4se2.pi.repositories.CompetitionRepository;

import java.util.List;

@Service
public class CompetitionService {
    private final CompetitionRepository competitionRepository;

    public CompetitionService(CompetitionRepository competitionRepository) {
        this.competitionRepository = competitionRepository;
    }

    public Competition create(Competition competition) {
        return competitionRepository.save(competition);
    }

    public Competition update(Long id, Competition competition) {
        competition.setId(id);
        return competitionRepository.save(competition);
    }

    public List<Competition> getAll() {
        return competitionRepository.findAll();
    }

    public Competition getById(Long id) {
        return competitionRepository.findById(id).orElseThrow(() -> new RuntimeException("Competition not found"));
    }

    public void delete(Long id) {
        competitionRepository.deleteById(id);
    }
}
