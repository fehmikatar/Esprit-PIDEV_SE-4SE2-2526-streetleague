package tn.esprit._4se2.pi.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import tn.esprit._4se2.pi.Enum.JoinRequestStatus;
import tn.esprit._4se2.pi.Enum.TeamRole;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JoinRequestActionRequest {

    @NotNull(message = "Action status is required (APPROVED or REJECTED)")
    private JoinRequestStatus status;

    private TeamRole teamRole;
}
