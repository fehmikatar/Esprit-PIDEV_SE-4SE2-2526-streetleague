package tn.esprit._4se2.pi.dto.Team;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamCurrentUserContext {

    private boolean isMember;
    private boolean isCaptain;
    private boolean hasPendingRequest;
}
