package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Builder
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "pricing_rules")
@Entity(name = "PricingRule")
public class PricingRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    DayOfWeek dayOfWeek;
    LocalTime startTime;
    LocalTime endTime;
    double pricePerHour;

    @ManyToOne
    SportSpace sportSpace;
}