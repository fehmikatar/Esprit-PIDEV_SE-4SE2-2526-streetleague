package tn.esprit._4se2.pi.services.Performance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit._4se2.pi.dto.Performance.PerformanceRequest;
import tn.esprit._4se2.pi.dto.Performance.PerformanceResponse;
import tn.esprit._4se2.pi.entities.Performance;
import tn.esprit._4se2.pi.exception.ResourceNotFoundException;
import tn.esprit._4se2.pi.mappers.PerformanceMapper;
import tn.esprit._4se2.pi.repositories.PerformanceRepository;
import tn.esprit._4se2.pi.repositories.PlayerRepository;
// Si vous avez accès au MatchRepository, décommentez la ligne suivante
// import tn.esprit._4se2.pi.repositories.MatchRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PerformanceService implements IPerformanceService {

    private final PerformanceRepository performanceRepository;
    private final PerformanceMapper performanceMapper;
    private final PlayerRepository playerRepository;
    // Si vous avez accès au MatchRepository, décommentez :
    // private final MatchRepository matchRepository;

    @Override
    public PerformanceResponse createPerformance(PerformanceRequest request) {
        log.info("Création d'une performance pour le joueur id: {}", request.getPlayerId());

        // Vérifier l'existence du joueur
        if (!playerRepository.existsById(request.getPlayerId())) {
            throw new ResourceNotFoundException("Joueur introuvable avec l'ID : " + request.getPlayerId());
        }

        // Vérifier l'existence du match (si vous avez le repository)
        // if (!matchRepository.existsById(request.getMatchId())) {
        //     throw new ResourceNotFoundException("Match introuvable avec l'ID : " + request.getMatchId());
        // }

        Performance performance = performanceMapper.toEntity(request);
        Performance saved = performanceRepository.save(performance);
        log.info("Performance créée avec l'id : {}", saved.getId());
        return performanceMapper.toResponse(saved);
    }

    @Override
    public List<PerformanceResponse> getAllPerformances() {
        log.debug("Récupération de toutes les performances");
        return performanceRepository.findAll().stream()
                .map(performanceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PerformanceResponse getPerformanceById(Long id) {
        log.debug("Recherche de la performance avec l'id : {}", id);
        Performance performance = performanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Performance non trouvée avec l'id : " + id));
        return performanceMapper.toResponse(performance);
    }

    @Override
    public PerformanceResponse updatePerformance(Long id, PerformanceRequest request) {
        Performance existing = performanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Performance non trouvée avec l'id : " + id));

        // Si le joueur change, vérifier son existence
        if (!existing.getPlayer().getId().equals(request.getPlayerId())) {
            if (!playerRepository.existsById(request.getPlayerId())) {
                throw new ResourceNotFoundException("Joueur introuvable avec l'ID : " + request.getPlayerId());
            }
            // Mise à jour du joueur dans l'entité (si votre mapper ne le fait pas)
            existing.setPlayer(playerRepository.getReferenceById(request.getPlayerId()));
        }

        // Si le match change, vérifier son existence (si MatchRepository disponible)
        // if (!existing.getMatchId().equals(request.getMatchId()) && !matchRepository.existsById(request.getMatchId())) {
        //     throw new ResourceNotFoundException("Match introuvable avec l'ID : " + request.getMatchId());
        // }

        performanceMapper.updateEntity(request, existing);
        Performance updated = performanceRepository.save(existing);
        log.info("Performance mise à jour avec l'id : {}", id);
        return performanceMapper.toResponse(updated);
    }

    @Override
    public void deletePerformance(Long id) {
        if (!performanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Performance non trouvée avec l'id : " + id);
        }
        performanceRepository.deleteById(id);
        log.info("Suppression de la performance id : {}", id);
    }
}