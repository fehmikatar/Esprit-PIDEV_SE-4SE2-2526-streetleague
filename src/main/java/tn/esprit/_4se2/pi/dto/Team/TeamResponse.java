package tn.esprit._4se2.pi.dto.Team;

import lombok.*;
import tn.esprit._4se2.pi.Enum.TeamStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamResponse {

    private Long id;
    private String name;
    private String sport;
    private String level;
    private String description;
    private String city;
    private String logo;
    private TeamStatus status;
    private LocalDateTime createdAt;

    // Creator info
    private Long captainId;
    private String captainFirstName;
    private String captainLastName;

    // Aggregate
    private int membersCount;

    // Context for the currently authenticated user
    private TeamCurrentUserContext currentUserContext;
}
