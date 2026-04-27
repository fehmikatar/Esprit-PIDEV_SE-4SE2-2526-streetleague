package tn.esprit._4se2.pi.entities;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit._4se2.pi.entities.enums.PlayPosition;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PositionRequirement {

    @Enumerated(EnumType.STRING)
    private PlayPosition position;

    private Integer neededCount;
}
