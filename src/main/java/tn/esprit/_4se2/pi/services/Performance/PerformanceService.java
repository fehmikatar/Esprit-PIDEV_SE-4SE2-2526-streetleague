package tn.esprit._4se2.pi.services.Performance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.dto.Performance.PerformanceRequest;
import tn.esprit._4se2.pi.dto.Performance.PerformanceResponse;
import tn.esprit._4se2.pi.entities.Performance;
import tn.esprit._4se2.pi.entities.PlayerLevel;
import tn.esprit._4se2.pi.entities.Badge;
import tn.esprit._4se2.pi.exception.ResourceNotFoundException;
import tn.esprit._4se2.pi.mappers.PerformanceMapper;
import tn.esprit._4se2.pi.repositories.PerformanceRepository;
import tn.esprit._4se2.pi.repositories.PlayerRepository;
import tn.esprit._4se2.pi.repositories.BadgeRepository;
import tn.esprit._4se2.pi.services.PlayerLevel.IPlayerLevelService;
import tn.esprit._4se2.pi.services.BadgePlayer.IBadgePlayerService;
import tn.esprit._4se2.pi.utils.XpCalculator;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PerformanceService implements IPerformanceService {

    private final PerformanceRepository performanceRepository;
    private final PerformanceMapper performanceMapper;
    private final PlayerRepository playerRepository;
    private final IPlayerLevelService playerLevelService;
    private final BadgeRepository badgeRepository;
    @Lazy
    private final IBadgePlayerService badgePlayerService;

    @Override
    @Transactional
    public PerformanceResponse createPerformance(PerformanceRequest request) {
        log.info("Création d'une performance pour le joueur id: {}", request.getPlayerId());

        if (!playerRepository.existsById(request.getPlayerId())) {
            throw new ResourceNotFoundException("Joueur introuvable avec l'ID : " + request.getPlayerId());
        }

        Performance performance = performanceMapper.toEntity(request);
        Performance saved = performanceRepository.save(performance);

        // Calcul XP via utilitaire
        int xpGained = XpCalculator.calculateXpGained(saved);
        PlayerLevel playerLevel = playerLevelService.addXp(saved.getPlayer(), xpGained);

        // Attribution des badges non encore obtenus
        List<Badge> badgesToAward = badgeRepository.findByRequiredXpLessThanEqual(playerLevel.getTotalXp());
        for (Badge badge : badgesToAward) {
            badgePlayerService.awardBadgeToPlayer(saved.getPlayer(), badge, saved);
        }

        log.info("Performance créée avec l'id : {}", saved.getId());
        return performanceMapper.toResponse(saved);
    }

    @Override
    public List<PerformanceResponse> getAllPerformances() {
        return performanceRepository.findAll().stream()
                .map(performanceMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PerformanceResponse getPerformanceById(Long id) {
        Performance performance = performanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Performance non trouvée avec l'id : " + id));
        return performanceMapper.toResponse(performance);
    }

    @Override
    @Transactional
    public PerformanceResponse updatePerformance(Long id, PerformanceRequest request) {
        Performance existing = performanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Performance non trouvée avec l'id : " + id));

        if (!existing.getPlayer().getId().equals(request.getPlayerId())) {
            if (!playerRepository.existsById(request.getPlayerId())) {
                throw new ResourceNotFoundException("Joueur introuvable avec l'ID : " + request.getPlayerId());
            }
            existing.setPlayer(playerRepository.getReferenceById(request.getPlayerId()));
        }

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