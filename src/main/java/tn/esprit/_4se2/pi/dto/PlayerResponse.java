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
public class PlayerResponse {
    Long id;
    String firstName;
    String lastName;
    String email;
    String phone;
    Integer skillLevel;
    String position;
    String teamName;
    Integer gamesPlayed;
    Double rating;
    LocalDateTime createdAt;
    Boolean isActive;
}