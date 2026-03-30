package tn.esprit._4se2.pi.services;

import tn.esprit._4se2.pi.dto.BadgeRequest;
import tn.esprit._4se2.pi.dto.BadgeResponse;
import java.util.List;

public interface IBadgeService {
    BadgeResponse createBadge(BadgeRequest request);
    List<BadgeResponse> getAllBadges();
    BadgeResponse getBadgeById(Long id);
    BadgeResponse updateBadge(Long id, BadgeRequest request);
    void deleteBadge(Long id);
}