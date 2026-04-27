package tn.esprit._4se2.pi.mappers;

import org.springframework.stereotype.Component;
import tn.esprit._4se2.pi.dto.Player.PlayerRequest;
import tn.esprit._4se2.pi.dto.Player.PlayerResponse;
import tn.esprit._4se2.pi.entities.Player;
import java.time.LocalDateTime;

@Component
public class PlayerMapper {

    public Player toEntity(PlayerRequest request) {
        if (request == null) return null;

        Player player = new Player();
<<<<<<< Updated upstream
        player.setFirstName(request.getFirstName());
        player.setLastName(request.getLastName());
        player.setEmail(request.getEmail());
        player.setPhone(request.getPhone());
        player.setPasswordHash(request.getPassword());
        player.setSkillLevel(request.getSkillLevel());
        player.setPosition(request.getPosition());
=======
        // User fields are set in the service AFTER userRepository.save()
        // Only set Player-specific fields here
        if (request.getSkillLevel() != null) {
            int index = Math.max(0, Math.min(request.getSkillLevel() - 1, tn.esprit._4se2.pi.entities.enums.SkillLevel.values().length - 1));
            player.setSkillLevel(tn.esprit._4se2.pi.entities.enums.SkillLevel.values()[index]);
        }
        if (request.getPosition() != null) {
            try {
                player.setPosition(tn.esprit._4se2.pi.entities.enums.PlayPosition.valueOf(request.getPosition().toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Ignore or log error
            }
        }
>>>>>>> Stashed changes
        player.setGamesPlayed(request.getGamesPlayed() != null ? request.getGamesPlayed() : 0);
        player.setRating(request.getRating() != null ? request.getRating() : 0.0);
        player.setCreatedAt(LocalDateTime.now());
        player.setIsActive(true);
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
                .profileImageUrl(entity.getProfileImageUrl())
                .skillLevel(entity.getSkillLevel() != null ? entity.getSkillLevel().ordinal() + 1 : 1)
                .position(entity.getPosition() != null ? entity.getPosition().name() : "")
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
<<<<<<< Updated upstream
        player.setSkillLevel(request.getSkillLevel());
        player.setPosition(request.getPosition());
        if (request.getGamesPlayed() != null) {
            player.setGamesPlayed(request.getGamesPlayed());
        }
        if (request.getRating() != null) {
            player.setRating(request.getRating());
        }
=======
        if (request.getSkillLevel() != null) {
            int index = Math.max(0, Math.min(request.getSkillLevel() - 1, tn.esprit._4se2.pi.entities.enums.SkillLevel.values().length - 1));
            player.setSkillLevel(tn.esprit._4se2.pi.entities.enums.SkillLevel.values()[index]);
        }
        if (request.getPosition() != null) {
            try {
                player.setPosition(tn.esprit._4se2.pi.entities.enums.PlayPosition.valueOf(request.getPosition().toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Ignore or log error
            }
        }
        if (request.getGamesPlayed() != null) player.setGamesPlayed(request.getGamesPlayed());
        if (request.getRating() != null) player.setRating(request.getRating());
>>>>>>> Stashed changes
    }
}