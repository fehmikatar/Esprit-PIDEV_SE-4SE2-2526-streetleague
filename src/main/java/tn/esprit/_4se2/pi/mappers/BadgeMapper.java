package tn.esprit._4se2.pi.mappers;

import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.dto.Badge.BadgeRequest;
import tn.esprit._4se2.pi.dto.Badge.BadgeResponse;
import tn.esprit._4se2.pi.entities.Badge;

@Component
public class BadgeMapper {

    public Badge toEntity(BadgeRequest request) {
        Badge badge = new Badge();
        badge.setName(request.getName());
        badge.setDescription(request.getDescription());
        badge.setLevel(request.getLevel());
        badge.setRequiredXp(request.getRequiredXp());
        badge.setIconUrl(request.getIconUrl());
        return badge;
    }

    public BadgeResponse toResponse(Badge badge) {
        return BadgeResponse.builder()
                .id(badge.getId())
                .name(badge.getName())
                .description(badge.getDescription())
                .level(badge.getLevel())
                .requiredXp(badge.getRequiredXp())
                .iconUrl(badge.getIconUrl())
                .build();
    }

    public void updateEntity(BadgeRequest request, Badge badge) {
        badge.setName(request.getName());
        badge.setDescription(request.getDescription());
        badge.setLevel(request.getLevel());
        badge.setRequiredXp(request.getRequiredXp());
        badge.setIconUrl(request.getIconUrl());
    }
}