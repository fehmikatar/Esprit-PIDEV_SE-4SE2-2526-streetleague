package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import tn.esprit._4se2.pi.entities.converters.SportTypeConverter;
import tn.esprit._4se2.pi.entities.enums.SkillLevel;
import tn.esprit._4se2.pi.entities.enums.PlayPosition;
import tn.esprit._4se2.pi.entities.enums.SportType;
import tn.esprit._4se2.pi.entities.enums.PlayStyle;

import java.util.List;

@Entity
@Table(name = "players")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Player extends User {

    @Enumerated(EnumType.STRING)
    SkillLevel skillLevel;

    @Enumerated(EnumType.STRING)
    PlayPosition position;

    @Convert(converter = SportTypeConverter.class)
    SportType sportType;

    @Enumerated(EnumType.STRING)
    PlayStyle preferredPlayStyle;

    Integer gamesPlayed;

    Double rating;

    String city;

    Double latitude;

    Double longitude;

    @ElementCollection
    @CollectionTable(name = "player_availability", joinColumns = @JoinColumn(name = "player_id"))
    List<AvailabilitySlot> availability;
}