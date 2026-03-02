package tn.esprit._4se2.pi.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit._4se2.pi.entities.Performance;
import tn.esprit._4se2.pi.repositories.PerformanceRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PerformanceService {

    private final PerformanceRepository performanceRepository;

    // Create
    public Performance createPerformance(Performance performance) {
        log.info("Création d'une nouvelle performance pour le joueur id: {}",
                performance.getPlayer() != null ? performance.getPlayer().getId() : "null");
        return performanceRepository.save(performance);
    }

    // Read all
    public List<Performance> getAllPerformances() {
        log.debug("Récupération de toutes les performances");
        return performanceRepository.findAll();
    }

    // Read by id
    public Performance getPerformanceById(Long id) {
        log.debug("Recherche de la performance avec l'id : {}", id);
        return performanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Performance non trouvée avec l'id : " + id));
    }

    // Update
    public Performance updatePerformance(Long id, Performance performanceDetails) {
        Performance existing = getPerformanceById(id);
        existing.setPlayer(performanceDetails.getPlayer());   // mise à jour de la relation ManyToOne
        existing.setMatchId(performanceDetails.getMatchId());
        existing.setScore(performanceDetails.getScore());
        existing.setAssists(performanceDetails.getAssists());
        existing.setDistanceCovered(performanceDetails.getDistanceCovered());
        existing.setTimePlayed(performanceDetails.getTimePlayed());
        existing.setRating(performanceDetails.getRating());
        log.info("Mise à jour de la performance id : {}", id);
        return performanceRepository.save(existing);
    }

    // Delete
    public void deletePerformance(Long id) {
        Performance performance = getPerformanceById(id);
        performanceRepository.delete(performance);
        log.info("Suppression de la performance id : {}", id);
    }
}