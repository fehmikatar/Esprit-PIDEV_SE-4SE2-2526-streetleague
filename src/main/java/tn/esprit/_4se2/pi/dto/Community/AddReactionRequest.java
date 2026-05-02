package tn.esprit._4se2.pi.dto.Community;

import lombok.Data;
import tn.esprit._4se2.pi.entities.ReactionType;

@Data
public class AddReactionRequest {
    private ReactionType reactionType;
}