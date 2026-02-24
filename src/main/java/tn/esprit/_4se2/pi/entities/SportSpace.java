package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "sport_spaces")
@Entity(name = "SportSpace")
public class SportSpace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String name;
    String description;
    String address;
    String city;
    double latitude;
    double longitude;
    String sportType;
    String status;

    LocalDateTime createdAt;

    @ManyToOne
    FieldOwner owner;

    @OneToMany(mappedBy = "sportSpace")
    List<Booking> bookings;
}