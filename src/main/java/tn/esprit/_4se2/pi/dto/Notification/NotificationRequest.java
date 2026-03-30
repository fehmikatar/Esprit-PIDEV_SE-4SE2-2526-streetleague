package tn.esprit._4se2.pi.dto.Notification;

import lombok.*;
import lombok.experimental.FieldDefaults;
import jakarta.validation.constraints.*;
import tn.esprit._4se2.pi.dto.User.UserRequest;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class NotificationRequest {

    @NotNull(message = "User ID is required")
    Long userId;

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100)
    String title;

    @NotBlank(message = "Message is required")
    @Size(min = 10, max = 1000)
    String message;

    @NotBlank(message = "Type is required")
    String type; // BOOKING, FEEDBACK, SYSTEM, PROMOTION
}