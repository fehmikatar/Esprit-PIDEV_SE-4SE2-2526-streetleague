package tn.esprit._4se2.pi.services.TeamManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit._4se2.pi.dto.TeamManagerRequest;
import tn.esprit._4se2.pi.dto.TeamManagerResponse;
import tn.esprit._4se2.pi.entities.TeamManager;
import tn.esprit._4se2.pi.mappers.TeamManagerMapper;
import tn.esprit._4se2.pi.repositories.TeamManagerRepository;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class TeamManagerService implements ITeamManagerService {

    private final TeamManagerRepository teamManagerRepository;
    private final TeamManagerMapper teamManagerMapper;

    @Override
    public TeamManagerResponse createTeamManager(TeamManagerRequest request) {
        log.info("Creating team manager with team code: {}", request.getTeamCode());

        if (teamManagerRepository.findByTeamCode(request.getTeamCode()).isPresent()) {
            throw new RuntimeException("Team manager with code " + request.getTeamCode() + " already exists");
        }

        TeamManager teamManager = teamManagerMapper.toEntity(request);
        TeamManager savedTeamManager = teamManagerRepository.save(teamManager);
        log.info("Team manager created successfully with id: {}", savedTeamManager.getId());

        return teamManagerMapper.toResponse(savedTeamManager);
    }

    @Override
    @Transactional(readOnly = true)
    public TeamManagerResponse getTeamManagerById(Long id) {
        log.info("Fetching team manager with id: {}", id);
        return teamManagerRepository.findById(id)
                .map(teamManagerMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Team manager not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamManagerResponse> getAllTeamManagers() {
        log.info("Fetching all team managers");
        return teamManagerRepository.findAll()
                .stream()
                .map(teamManagerMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TeamManagerResponse getTeamManagerByTeamCode(String teamCode) {
        log.info("Fetching team manager with team code: {}", teamCode);
        return teamManagerRepository.findByTeamCode(teamCode)
                .map(teamManagerMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Team manager not found with code: " + teamCode));
    }

    @Override
    public TeamManagerResponse updateTeamManager(Long id, TeamManagerRequest request) {
        log.info("Updating team manager with id: {}", id);

        TeamManager teamManager = teamManagerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team manager not found with id: " + id));

        teamManagerMapper.updateEntity(request, teamManager);
        TeamManager updatedTeamManager = teamManagerRepository.save(teamManager);
        log.info("Team manager updated successfully with id: {}", id);

        return teamManagerMapper.toResponse(updatedTeamManager);
    }

    @Override
    public void deleteTeamManager(Long id) {
        log.info("Deleting team manager with id: {}", id);

        if (!teamManagerRepository.existsById(id)) {
            throw new RuntimeException("Team manager not found with id: " + id);
        }

        teamManagerRepository.deleteById(id);
        log.info("Team manager deleted successfully with id: {}", id);
    }
}