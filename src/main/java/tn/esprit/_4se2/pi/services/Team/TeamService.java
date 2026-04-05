package tn.esprit._4se2.pi.services.Team;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import tn.esprit._4se2.pi.entities.Team;
import tn.esprit._4se2.pi.repositories.TeamRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamService implements TeamServiceInterface {

    private final TeamRepository teamRepository;

    @Override
    public Team create(Team team) {
        return teamRepository.save(team);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Team> getAll() {
        return teamRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Team getById(Long id) {
        return teamRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public Team getByName(String name) {
        return teamRepository.findByName(name);
    }

    @Override
    public Team update(Long id, Team updatedTeam) {
        Team existing = teamRepository.findById(id).orElse(null);
        if (existing == null) {
            return null;
        }

        existing.setName(updatedTeam.getName());
        existing.setSport(updatedTeam.getSport());
        existing.setLevel(updatedTeam.getLevel());
        existing.setDescription(updatedTeam.getDescription());
        existing.setCity(updatedTeam.getCity());
        existing.setLogo(updatedTeam.getLogo());
        existing.setStatus(updatedTeam.getStatus());

        return teamRepository.save(existing);
    }

    @Override
    public void delete(Long id) {
        teamRepository.deleteById(id);
    }
}