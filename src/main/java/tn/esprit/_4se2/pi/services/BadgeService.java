package tn.esprit._4se2.pi.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tn.esprit._4se2.pi.entities.Badge;
import tn.esprit._4se2.pi.repositories.BadgeRepository;

import java.util.List;

@Slf4j          // Lombok : ajoute un logger
@Service
@RequiredArgsConstructor // Lombok : génère un constructeur avec tous les champs final (injection)
public class BadgeService {

    private final BadgeRepository badgeRepository;

    // Create
    public Badge createBadge(Badge badge) {
        log.info("Création d'un nouveau badge : {}", badge.getName());
        return badgeRepository.save(badge);
    }

    // Read all
    public List<Badge> getAllBadges() {
        log.debug("Récupération de tous les badges");
        return badgeRepository.findAll();
    }

    // Read by id
    public Badge getBadgeById(Long id) {
        log.debug("Recherche du badge avec l'id : {}", id);
        return badgeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Badge non trouvé avec l'id : " + id));
    }

    // Update
    public Badge updateBadge(Long id, Badge badgeDetails) {
        Badge existingBadge = getBadgeById(id);
        existingBadge.setName(badgeDetails.getName());
        existingBadge.setDescription(badgeDetails.getDescription());
        existingBadge.setLevel(badgeDetails.getLevel());
        existingBadge.setRequiredXp(badgeDetails.getRequiredXp());
        existingBadge.setIconUrl(badgeDetails.getIconUrl());
        // La relation badgePlayers n'est pas modifiée ici (gérée à part)
        log.info("Mise à jour du badge id : {}", id);
        return badgeRepository.save(existingBadge);
    }

    // Delete
    public void deleteBadge(Long id) {
        Badge badge = getBadgeById(id);
        badgeRepository.delete(badge);
        log.info("Suppression du badge id : {}", id);
    }
}