package tn.esprit._4se2.pi.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TeamManagerResponse {
    Long id;
    String firstName;
    String lastName;
    String email;
    String phone;
    String teamName;
    String teamCode;
    Integer memberCount;
    List<Long> memberIds;
    String experience;
    LocalDateTime createdAt;
    Boolean isActive;
}