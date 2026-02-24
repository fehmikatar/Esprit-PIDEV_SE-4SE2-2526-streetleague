package tn.esprit._4se2.pi.services.Player;

import tn.esprit._4se2.pi.dto.PlayerRequest;
import tn.esprit._4se2.pi.dto.PlayerResponse;
import java.util.List;

public interface IPlayerService {
    PlayerResponse createPlayer(PlayerRequest request);
    PlayerResponse getPlayerById(Long id);
    List<PlayerResponse> getAllPlayers();
    List<PlayerResponse> getPlayersBySkillLevel(Integer skillLevel);
    List<PlayerResponse> getPlayersByPosition(String position);
    PlayerResponse updatePlayer(Long id, PlayerRequest request);
    void deletePlayer(Long id);
}