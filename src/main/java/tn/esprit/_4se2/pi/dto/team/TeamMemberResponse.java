package tn.esprit._4se2.pi.dto;

import lombok.*;
import tn.esprit._4se2.pi.Enum.MemberStatus;
import tn.esprit._4se2.pi.Enum.TeamRole;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeamMemberResponse {

    private Long memberId;
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private TeamRole teamRole;
    private LocalDateTime joinedAt;
    private MemberStatus status;
}
