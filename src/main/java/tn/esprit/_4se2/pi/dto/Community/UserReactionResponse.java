package tn.esprit._4se2.pi.dto.Community;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit._4se2.pi.entities.ReactionType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserReactionResponse {
    private Long userId;
    private String firstName;
    private String lastName;
    private String profileImageUrl;
    private ReactionType reactionType;
}
