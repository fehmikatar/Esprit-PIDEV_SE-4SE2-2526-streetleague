package tn.esprit._4se2.pi.services.Badge;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit._4se2.pi.dto.Badge.BadgeRequest;
import tn.esprit._4se2.pi.dto.Badge.BadgeResponse;
import tn.esprit._4se2.pi.entities.Badge;
import tn.esprit._4se2.pi.exception.DuplicateResourceException;
import tn.esprit._4se2.pi.exception.ResourceNotFoundException;
import tn.esprit._4se2.pi.mappers.BadgeMapper;
import tn.esprit._4se2.pi.repositories.BadgeRepository;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BadgeService implements IBadgeService {

    private final BadgeRepository badgeRepository;
    private final BadgeMapper badgeMapper;

    @Override
    public BadgeResponse createBadge(BadgeRequest request) {
        log.info("Création d'un nouveau badge : {}", request.getName());

        // Vérification d'unicité
        if (badgeRepository.findByName(request.getName()).isPresent()) {
            throw new DuplicateResourceException("Un badge avec le nom '" + request.getName() + "' existe déjà");
        }

        Badge badge = badgeMapper.toEntity(request);
        Badge saved = badgeRepository.save(badge);
        log.info("Badge créé avec l'id : {}", saved.getId());
        return badgeMapper.toResponse(saved);
    }

    @Override
    public List<BadgeResponse> getAllBadges() {
        log.debug("Récupération de tous les badges");
        return badgeRepository.findAll().stream()
                .map(badgeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BadgeResponse getBadgeById(Long id) {
        log.debug("Recherche du badge avec l'id : {}", id);
        Badge badge = badgeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Badge non trouvé avec l'id : " + id));
        return badgeMapper.toResponse(badge);
    }

    @Override
    public BadgeResponse updateBadge(Long id, BadgeRequest request) {
        Badge existing = badgeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Badge non trouvé avec l'id : " + id));

        // Vérification d'unicité si le nom a changé
        badgeRepository.findByName(request.getName())
                .ifPresent(b -> {
                    if (!b.getId().equals(id)) {
                        throw new DuplicateResourceException("Un badge avec le nom '" + request.getName() + "' existe déjà");
                    }
                });

        badgeMapper.updateEntity(request, existing);
        Badge updated = badgeRepository.save(existing);
        log.info("Badge mis à jour avec l'id : {}", id);
        return badgeMapper.toResponse(updated);
    }

    @Override
    public void deleteBadge(Long id) {
        if (!badgeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Badge non trouvé avec l'id : " + id);
        }
        badgeRepository.deleteById(id);
        log.info("Suppression du badge id : {}", id);
    }
}