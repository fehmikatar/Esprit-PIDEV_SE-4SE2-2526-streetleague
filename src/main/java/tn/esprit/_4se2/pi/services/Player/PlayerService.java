package tn.esprit._4se2.pi.services.Player;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.dto.PlayerRequest;
import tn.esprit._4se2.pi.dto.PlayerResponse;
import tn.esprit._4se2.pi.entities.Player;
import tn.esprit._4se2.pi.mappers.PlayerMapper;
import tn.esprit._4se2.pi.repositories.PlayerRepository;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PlayerService implements IPlayerService {

    private final PlayerRepository playerRepository;
    private final PlayerMapper playerMapper;

    @Override
    public PlayerResponse createPlayer(PlayerRequest request) {
        log.info("Creating player with email: {}", request.getEmail());

        Player player = playerMapper.toEntity(request);
        Player savedPlayer = playerRepository.save(player);
        log.info("Player created successfully with id: {}", savedPlayer.getId());

        return playerMapper.toResponse(savedPlayer);
    }

    @Override
    @Transactional(readOnly = true)
    public PlayerResponse getPlayerById(Long id) {
        log.info("Fetching player with id: {}", id);
        return playerRepository.findById(id)
                .map(playerMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Player not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlayerResponse> getAllPlayers() {
        log.info("Fetching all players");
        return playerRepository.findAll()
                .stream()
                .map(playerMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlayerResponse> getPlayersBySkillLevel(Integer skillLevel) {
        log.info("Fetching players with skill level: {}", skillLevel);
        return playerRepository.findBySkillLevel(skillLevel)
                .stream()
                .map(playerMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlayerResponse> getPlayersByPosition(String position) {
        log.info("Fetching players with position: {}", position);
        return playerRepository.findByPosition(position)
                .stream()
                .map(playerMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public PlayerResponse updatePlayer(Long id, PlayerRequest request) {
        log.info("Updating player with id: {}", id);

        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Player not found with id: " + id));

        playerMapper.updateEntity(request, player);
        Player updatedPlayer = playerRepository.save(player);
        log.info("Player updated successfully with id: {}", id);

        return playerMapper.toResponse(updatedPlayer);
    }

    @Override
    public void deletePlayer(Long id) {
        log.info("Deleting player with id: {}", id);

        if (!playerRepository.existsById(id)) {
            throw new RuntimeException("Player not found with id: " + id);
        }

        playerRepository.deleteById(id);
        log.info("Player deleted successfully with id: {}", id);
    }
}