package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.math.BigDecimal;

@Entity
@Table(name = "sport_spaces")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SportSpace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    Long fieldOwnerId;

    @Column(nullable = false, length = 100)
    String name;

    @Column(length = 500)
    String description;

    @Column(nullable = false)
    String address;

    @Column(nullable = false)
    String location;

    @Column(nullable = false)
    String sportType;

    @Column(nullable = false)
    Integer capacity;

    @Column(nullable = false, precision = 10, scale = 2)
    BigDecimal hourlyRate;

    @Column(length = 500)
    String amenities;

    @Column
    Double latitude;

    @Column
    Double longitude;

    @Column(name = "is_available")
    @Builder.Default
    Boolean isAvailable = true;
}