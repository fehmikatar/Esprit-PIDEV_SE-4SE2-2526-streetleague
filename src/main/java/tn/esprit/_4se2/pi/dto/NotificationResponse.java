package tn.esprit._4se2.pi.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationResponse {
    Long id;
    Long userId;
    String title;
    String message;
    String type;
    Boolean isRead;
    LocalDateTime createdAt;
    LocalDateTime readAt;
}