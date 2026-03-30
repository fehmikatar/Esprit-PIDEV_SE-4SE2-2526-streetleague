package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "team_bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TeamBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    Long teamId;

    @Column(nullable = false)
    Long sportSpaceId;

    @Column(name = "booking_date")
    LocalDateTime bookingDate;

    @Column(name = "start_time", nullable = false)
    LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    LocalDateTime endTime;

    @Column(nullable = false)
    Integer playerCount;

    @Column(precision = 10, scale = 2)
    BigDecimal totalPrice;

    @Column(nullable = false)
    @Builder.Default
    String status = "PENDING";

    @Column(name = "created_at")
    LocalDateTime createdAt;
}