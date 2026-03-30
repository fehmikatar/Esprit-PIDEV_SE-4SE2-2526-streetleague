package tn.esprit._4se2.pi.dto;

import lombok.*;
import tn.esprit._4se2.pi.Enum.CommunityMemberRole;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityMemberResponse {
    private Long id;
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private CommunityMemberRole role;
    private LocalDateTime joinedAt;
}
