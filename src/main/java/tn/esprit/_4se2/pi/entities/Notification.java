package tn.esprit._4se2.pi.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    Long userId;

    @Column(nullable = false)
    String title;

    @Column(nullable = false, length = 1000)
    String message;

    @Column(nullable = false)
    String type;

    @Column(nullable = false)
    @Builder.Default
    Boolean isRead = false;

    @Column(name = "created_at")
    LocalDateTime createdAt;

    @Column(name = "read_at")
    LocalDateTime readAt;
}