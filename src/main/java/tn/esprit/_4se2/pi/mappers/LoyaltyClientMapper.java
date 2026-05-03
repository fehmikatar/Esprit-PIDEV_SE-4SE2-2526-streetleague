package tn.esprit._4se2.pi.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.dto.Loyalty.LoyaltyClientResponse;
import tn.esprit._4se2.pi.entities.LoyaltyClient;
import tn.esprit._4se2.pi.services.User.UserService; // hypothétique

@Component
@RequiredArgsConstructor
public class LoyaltyClientMapper {

    private final UserService userService; // pour récupérer le nom complet

    public LoyaltyClientResponse toResponse(LoyaltyClient client) {
        String userFullName = userService.getUserFullName(client.getUser().getId());
        return LoyaltyClientResponse.builder()
                .id(client.getId())
                .userId(client.getUser().getId())
                .userFullName(userFullName)
                .currentTierId(client.getCurrentTier() != null ? client.getCurrentTier().getId() : null)
                .currentTierName(client.getCurrentTier() != null ? client.getCurrentTier().getName() : null)
                .totalPoints(client.getTotalPoints())
                .joinedAt(client.getJoinedAt())
                .tierValidUntil(client.getTierValidUntil())
                .build();
    }
}