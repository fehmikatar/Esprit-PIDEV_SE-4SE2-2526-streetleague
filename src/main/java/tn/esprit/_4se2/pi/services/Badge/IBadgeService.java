package tn.esprit._4se2.pi.services.Badge;

import tn.esprit._4se2.pi.dto.Badge.BadgeRequest;
import tn.esprit._4se2.pi.dto.Badge.BadgeResponse;
import java.util.List;

public interface IBadgeService {
    BadgeResponse createBadge(BadgeRequest request);
    List<BadgeResponse> getAllBadges();
    BadgeResponse getBadgeById(Long id);
    BadgeResponse updateBadge(Long id, BadgeRequest request);
    void deleteBadge(Long id);
}