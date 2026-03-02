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
public class FeedbackResponse {
    Long id;
    Long userId;
    String userName;
    Long sportSpaceId;
    String sportSpaceName;
    Integer rating;
    String comment;
    String status;
    LocalDateTime createdAt;
    LocalDateTime approvedAt;
}