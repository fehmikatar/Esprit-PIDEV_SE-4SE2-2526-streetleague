package tn.esprit._4se2.pi.dto.Community;

import lombok.AllArgsConstructor;
import lombok.Data;
import tn.esprit._4se2.pi.entities.ReactionType;

@Data
@AllArgsConstructor
public class ReactionSummary {
    private ReactionType reactionType;
    private String emoji;
    private Long count;
}