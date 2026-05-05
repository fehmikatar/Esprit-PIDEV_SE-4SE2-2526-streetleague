package tn.esprit._4se2.pi.services;

import org.springframework.stereotype.Service;
import tn.esprit._4se2.pi.entities.Advertisement;
import tn.esprit._4se2.pi.repositories.AdvertisementRepository;

import java.util.List;

@Service
public class AdvertisementService {

    private final AdvertisementRepository repository;

    public AdvertisementService(AdvertisementRepository repository) {
        this.repository = repository;
    }

    public Advertisement create(Advertisement ad) {
        return repository.save(ad);
    }

    public List<Advertisement> getAll() {
        return repository.findAll();
    }

    public Advertisement getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ad not found"));
    }

    public Advertisement update(Long id, Advertisement ad) {
        Advertisement existing = getById(id);
        ad.setId(existing.getId());
        ad.setCreatedAt(existing.getCreatedAt());
        return repository.save(ad);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public void incrementClicks(Long id) {
        Advertisement ad = getById(id);
        ad.setClicks(ad.getClicks() + 1);
        repository.save(ad);
    }

    public void incrementImpressions(Long id) {
        Advertisement ad = getById(id);
        ad.setImpressions(ad.getImpressions() + 1);
        repository.save(ad);
    }

    public List<Advertisement> getByCompetition(Long competitionId) {
        return repository.findByCompetitionIdAndIsActiveTrue(competitionId);
    }

    public List<Advertisement> getByMatch(Long matchId) {
        return repository.findByMatchIdAndIsActiveTrue(matchId);
    }

    public List<Advertisement> getByTeam(Long teamId) {
        return repository.findByTeamIdAndIsActiveTrue(teamId);
    }
}
