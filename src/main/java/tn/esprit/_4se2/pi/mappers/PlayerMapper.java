package tn.esprit._4se2.pi.mappers;

import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.dto.Player.PlayerRequest;
import tn.esprit._4se2.pi.dto.Player.PlayerResponse;
import tn.esprit._4se2.pi.entities.Player;

@Component
public class PlayerMapper {

    public Player toEntity(PlayerRequest request) {
        if (request == null) return null;

        Player player = new Player();
        // User fields are set in the service AFTER userRepository.save()
        // Only set Player-specific fields here
        player.setSkillLevel(request.getSkillLevel());
        player.setPosition(request.getPosition());
        player.setGamesPlayed(request.getGamesPlayed() != null ? request.getGamesPlayed() : 0);
        player.setRating(request.getRating() != null ? request.getRating() : 0.0);
        return player;
    }

    public PlayerResponse toResponse(Player entity) {
        if (entity == null) return null;

        return PlayerResponse.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .skillLevel(entity.getSkillLevel())
                .position(entity.getPosition())
                .gamesPlayed(entity.getGamesPlayed())
                .rating(entity.getRating())
                .createdAt(entity.getCreatedAt())
                .isActive(entity.getIsActive())
                .build();
    }

    public void updateEntity(PlayerRequest request, Player player) {
        if (request == null || player == null) return;

        player.setFirstName(request.getFirstName());
        player.setLastName(request.getLastName());
        player.setEmail(request.getEmail());
        player.setPhone(request.getPhone());
        player.setSkillLevel(request.getSkillLevel());
        player.setPosition(request.getPosition());
        if (request.getGamesPlayed() != null) player.setGamesPlayed(request.getGamesPlayed());
        if (request.getRating() != null) player.setRating(request.getRating());
    }
}