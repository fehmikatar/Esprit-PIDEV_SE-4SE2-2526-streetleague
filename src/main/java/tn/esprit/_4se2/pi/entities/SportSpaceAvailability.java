package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Entity
@Table(name = "sport_space_availabilities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SportSpaceAvailability {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    Long sportSpaceId;

    @Column(name = "available_from", nullable = false)
    LocalDateTime availableFrom;

    @Column(name = "available_to", nullable = false)
    LocalDateTime availableTo;

    @Column(nullable = false)
    Integer totalSlots;

    @Column(nullable = false)
    @Builder.Default
    Integer bookedSlots = 0;

    @Column(nullable = false)
    @Builder.Default
    String status = "AVAILABLE";
}