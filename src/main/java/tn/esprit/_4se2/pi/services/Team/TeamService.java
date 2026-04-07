package tn.esprit._4se2.pi.services.Team;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        if (isCurrentUserAdmin()) {
            return teamRepository.findAll();
        }
        return teamRepository.findAllSafe();
    }

    @Override
    @Transactional(readOnly = true)
    public Team getById(Long id) {
        if (isCurrentUserAdmin()) {
            return teamRepository.findById(id).orElse(null);
        }
        return teamRepository.findSafeById(id).orElse(null);
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

    private boolean isCurrentUserAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getAuthorities() == null) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }
}