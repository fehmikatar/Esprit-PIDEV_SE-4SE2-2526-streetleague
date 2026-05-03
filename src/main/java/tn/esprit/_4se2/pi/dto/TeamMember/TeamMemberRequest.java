package tn.esprit._4se2.pi.dto.TeamMember;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data

public class TeamMemberRequest {
    @NotNull(message = "userId est obligatoire")
    private Long userId;

    @NotNull(message = "teamId est obligatoire")
    private Long teamId;

    // exemple : rôle dans l'équipe (ADMIN, MEMBER, etc.)
    private String role;
}
