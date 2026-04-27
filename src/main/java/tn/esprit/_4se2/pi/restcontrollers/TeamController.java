package tn.esprit._4se2.pi.restcontrollers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.dto.Team.TeamDTOs.TeamResponseDTO;
import tn.esprit._4se2.pi.dto.Team.TeamDTOs.TeamMemberDTO;
import tn.esprit._4se2.pi.entities.Category;
import tn.esprit._4se2.pi.entities.Team;
import tn.esprit._4se2.pi.entities.Player;
import tn.esprit._4se2.pi.entities.TeamMember;
import tn.esprit._4se2.pi.repositories.CategoryRepository;
import tn.esprit._4se2.pi.repositories.UserRepository;
import tn.esprit._4se2.pi.services.Team.TeamService;
import tn.esprit._4se2.pi.services.TeamJoinRequest.TeamJoinRequestService;
import org.springframework.security.core.Authentication;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teams")
public class TeamController {

    private final TeamService teamService;
    private final TeamJoinRequestService teamJoinRequestService;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;


    public TeamController(TeamService teamService, TeamJoinRequestService teamJoinRequestService,
            CategoryRepository categoryRepository, UserRepository userRepository) {
        this.teamService = teamService;
        this.teamJoinRequestService = teamJoinRequestService;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    // Créer une équipe
    @PostMapping
    public Team create(@RequestBody Map<String, Object> payload) {
        Team team = new Team();

        team.setName(asString(payload.get("name")));
        team.setSport(tn.esprit._4se2.pi.entities.enums.SportType.FOOTBALL); // Map appropriately if generic parsing is
                                                                             // needed
        team.setLevel(tn.esprit._4se2.pi.entities.enums.SkillLevel.BEGINNER);
        team.setDescription(extractLabel(payload.get("description")));
        team.setCity(extractLabel(payload.get("city")));
        team.setLogo(asString(payload.get("logo")));
        Object categoryId = payload.get("categoryId");
        if (categoryId != null) {
            Long parsedCategoryId = Long.valueOf(String.valueOf(categoryId));
            Category category = categoryRepository.findById(parsedCategoryId).orElse(null);
            team.setCategory(category);
        }

        return teamService.create(team);
    }

    private String asString(Object value) {
        if (value == null) {
            return null;
        }
        String s = String.valueOf(value).trim();
        return s.isEmpty() ? null : s;
    }

    private String extractLabel(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof String s) {
            return s.trim().isEmpty() ? null : s.trim();
        }
        if (value instanceof Map<?, ?> map) {
            Object nom = map.get("nom");
            if (nom != null && !String.valueOf(nom).isBlank()) {
                return String.valueOf(nom).trim();
            }
            Object name = map.get("name");
            if (name != null && !String.valueOf(name).isBlank()) {
                return String.valueOf(name).trim();
            }
            Object valueField = map.get("value");
            if (valueField != null && !String.valueOf(valueField).isBlank()) {
                return String.valueOf(valueField).trim();
            }
        }
        return String.valueOf(value);
    }

    // Récupérer toutes les équipes
    @GetMapping
    public List<Team> getAll() {
        return teamService.getAll();
    }

    // Récupérer une équipe par son ID
    @GetMapping("/{id}")
    public Team getById(@PathVariable Long id) {
        return teamService.getById(id);
    }

    // Récupérer les détails d'une équipe avec les membres (pour la vue détail)
    @GetMapping("/{id}/details")
    public TeamResponseDTO getTeamDetails(@PathVariable Long id) {
        Team team = teamService.getById(id);
        return convertTeamToResponseDTO(team);
    }

    // Demander à rejoindre une équipe (PLAYER uniquement)
    @PostMapping("/{id}/join-requests")
    public Map<String, Object> requestJoinTeam(@PathVariable Long id,
            @RequestBody(required = false) Map<String, Object> payload) {
        String message = payload == null ? null : asString(payload.get("message"));
        return teamJoinRequestService.createJoinRequest(id, message);
    }

    // Endpoint officiel admin pour la page Manage Request
    @GetMapping("/join-requests")
    public List<Map<String, Object>> getPendingJoinRequestsForAdmin() {
        return teamJoinRequestService.getPendingRequestsForAdmin();
    }

    // Approver une demande d'adhésion (chemin exact utilisé par le frontend)
    @PatchMapping("/join-requests/{id}/approve")
    public Map<String, Object> approveJoinRequest(@PathVariable Long id) {
        return teamJoinRequestService.approveJoinRequest(id);
    }

    // Rejeter une demande d'adhésion (chemin exact utilisé par le frontend)
    @PostMapping("/join-requests/{id}/reject")
    public Map<String, Object> rejectJoinRequest(@PathVariable Long id) {
        return teamJoinRequestService.rejectJoinRequest(id);
    }

    // Récupérer une équipe par son nom
    @GetMapping("/name/{name}")
    public Team getByName(@PathVariable String name) {
        return teamService.getByName(name);
    }

    @GetMapping("/search/by-category-owner")
    public List<Team> searchByCategoryAndOwner(
            @RequestParam(defaultValue = "") String category,
            @RequestParam(defaultValue = "") String owner) {
        return teamService.searchByCategoryAndOwnerKeyword(category, owner);
    }

    // Mettre à jour une équipe
    @PutMapping("/{id}")
    public Team update(@PathVariable Long id, @RequestBody Team team) {
        return teamService.update(id, team);
    }

    // Supprimer une équipe
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        teamService.delete(id);
    }

    // Convert Team entity to TeamResponseDTO with members
    private TeamResponseDTO convertTeamToResponseDTO(Team team) {
        TeamResponseDTO dto = new TeamResponseDTO();

        dto.setId(team.getId());
        dto.setSport(team.getSport() != null ? team.getSport().name() : "");
        dto.setLevel(team.getLevel() != null ? team.getLevel().name() : "");
        dto.setDescription(team.getDescription());
        dto.setCity(team.getCity());
        dto.setCategoryId(team.getCategory() != null ? team.getCategory().getId() : null);
        dto.setCategoryName(team.getCategory() != null ? team.getCategory().getNom() : null);
        dto.setCity(team.getCity());
        dto.setStatus(team.getStatus().toString());
        dto.setCreatedAt(team.getCreatedAt());

        // Convert team members to DTOs
        List<TeamMemberDTO> memberDTOs = team.getMembers().stream()
                .map(this::convertTeamMemberToDTO)
                .collect(Collectors.toList());
        dto.setMembers(memberDTOs);

        return dto;
    }

    // Convert TeamMember entity to TeamMemberDTO
    private TeamMemberDTO convertTeamMemberToDTO(TeamMember member) {
        TeamMemberDTO dto = new TeamMemberDTO();

        dto.setUserId(member.getUser().getId());
        dto.setFirstName(member.getUser().getFirstName());
        dto.setLastName(member.getUser().getLastName());
        dto.setPhone(member.getUser().getPhone());
        dto.setProfileImageUrl(member.getUser().getProfileImageUrl());
        dto.setRole(member.getRole().toString());

        // If user is a Player, add player-specific information
        if (member.getUser() instanceof Player player) {
            dto.setSkillLevel(player.getSkillLevel() != null ? player.getSkillLevel().ordinal() + 1 : 1);
            dto.setPosition(player.getPosition() != null ? player.getPosition().name() : "");
            dto.setRating(player.getRating());
        }

        return dto;
    }

@PostMapping("/sync-communities")
public ResponseEntity<Void> syncCommunities(Authentication authentication) {
    Long userId = userRepository.findByEmail(authentication.getName())
        .orElseThrow().getId();
    teamJoinRequestService.syncUserCommunities(userId);
    return ResponseEntity.ok().build();
}
}
