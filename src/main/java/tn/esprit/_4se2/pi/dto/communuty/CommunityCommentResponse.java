package tn.esprit._4se2.pi.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityCommentResponse {

    private Long id;

    // Author info
    private Long authorId;
    private String authorFirstName;
    private String authorLastName;

    private String content;
    private LocalDateTime createdAt;
}
